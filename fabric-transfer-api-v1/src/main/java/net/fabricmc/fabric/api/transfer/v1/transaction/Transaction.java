/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.transfer.v1.transaction;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.transaction.TransactionManagerImpl;

/**
 * A global operation where participants guarantee atomicity: either the whole operation succeeds,
 * or it is completely aborted and rolled back.
 *
 * <p>One can imagine that transactions are like video game checkpoints.
 * <ul>
 *     <li>{@linkplain #openOuter Opening a transaction} with a try-with-resources block creates a checkpoint.</li>
 *     <li>Modifications to game state can then happen.</li>
 *     <li>Calling {@link #commit} validates the modifications that happened during the transaction,
 *     essentially discarding the checkpoint.</li>
 *     <li>Calling {@link #abort} or doing nothing and letting the transaction be {@linkplain #close closed} at the end
 *     of the try-with-resources block cancels any modification that happened during the transaction,
 *     reverting to the checkpoint.</li>
 *     <li>Calling {@link #openNested} on a transaction creates a new nested transaction, i.e. a new checkpoint with the current state.
 *     Committing a nested transaction will validate the changes that happened, but they may
 *     still be cancelled later if a parent transaction is cancelled.
 *     Aborting a nested transaction immediately reverts the changes - cancelling any modification made after the call
 *     to {@link #openNested}.</li>
 * </ul>
 *
 * <p>This is illustrated in the following example.
 * <pre>{@code
 * try (Transaction outerTransaction = Transaction.openOuter()) {
 *     // (A) some transaction operations
 *     try (Transaction nestedTransaction = outerTransaction.openNested()) {
 *         // (B) more operations
 *         nestedTransaction.commit(); // Validate the changes that happened in this transaction.
 *                                     // This is a nested transaction, so changes will only be applied if the outer
 *                                     // transaction is committed too.
 *     }
 *     // (C) even more operations
 *     outerTransaction.commit(); // This is an outer transaction: changes (A), (B) and (C) are applied.
 * }
 * // If we hadn't committed the outerTransaction, all changes (A), (B) and (C) would have been reverted.
 * }</pre>
 *
 * <p>Participants are responsible for upholding this contract themselves, by using {@link #addCloseCallback}
 * to react to transaction close events and properly validate or revert changes.
 * Any action that modifies state outside of the transaction, such as calls to {@code markDirty()} or neighbor updates,
 * should be deferred until {@linkplain #addOuterCloseCallback after the outer transaction is closed}
 * to give every participant a chance to react to transaction close events.
 *
 * <p>This is very low-level for most applications, and most participants should subclass {@link SnapshotParticipant}
 * that will take care of properly maintaining their state.
 *
 * <p>Every transaction is only valid on the thread it was opened on,
 * and attempts to call transaction functions on any other thread will throw an exception.
 * Consequently, transactions can be concurrent across multiple threads, as long as they don't share any state.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public interface Transaction extends AutoCloseable {
	/**
	 * Open a new outer transaction.
	 *
	 * @throws IllegalStateException If a transaction is already active on the current thread.
	 */
	static Transaction openOuter() {
		return TransactionManagerImpl.MANAGERS.get().openOuter();
	}

	/**
	 * @return True if a transaction is open on the current thread, and false otherwise.
	 */
	static boolean isOpen() {
		return TransactionManagerImpl.MANAGERS.get().isOpen();
	}

	/**
	 * Open a new nested transaction.
	 *
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 * @throws IllegalStateException If this transaction is not the current transaction.
	 * @throws IllegalStateException If this transaction was closed.
	 */
	Transaction openNested();

	/**
	 * Close the current transaction, rolling back all the changes that happened during this transaction and
	 * the transactions opened with {@link #openNested} from this transaction.
	 *
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 * @throws IllegalStateException If this transaction is not the current transaction.
	 * @throws IllegalStateException If this transaction was closed.
	 */
	void abort();

	/**
	 * Close the current transaction, committing all the changes that happened during this transaction and
	 * the <b>committed</b> transactions opened with {@link #openNested} from this transaction.
	 * If this transaction was opened with {@link #openOuter}, all changes are applied.
	 * If this transaction was opened with {@link #openNested}, all changes will be applied when and if the changes of
	 * the parent transactions are applied.
	 *
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 * @throws IllegalStateException If this transaction is not the current transaction.
	 * @throws IllegalStateException If this transaction was closed.
	 */
	void commit();

	/**
	 * Abort the current transaction if it was not closed already.
	 */
	@Override
	void close();

	/**
	 * @return The nesting depth of this transaction: 0 if it was opened with {@link #openOuter},
	 * 1 if its parent was opened with {@link #openOuter}, and so on...
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 */
	int nestingDepth();

	/**
	 * Return the transaction with the specific nesting depth.
	 *
	 * @param nestingDepth Queried nesting depth.
	 * @throws IndexOutOfBoundsException If there is no open transaction with the request nesting depth.
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 */
	Transaction getOpenTransaction(int nestingDepth);

	/**
	 * Register a callback that will be invoked when this transaction is closed.
	 * Registered callbacks are invoked last-to-first: the last callback to be registered will be the first to be invoked, and so on...
	 *
	 * <p>Updates that may change the state of other participants should be deferred until after the outermost transaction is closed
	 * using {@link #addOuterCloseCallback}.
	 *
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 */
	void addCloseCallback(CloseCallback closeCallback);

	/**
	 * A callback that is invoked when a transaction is closed.
	 */
	@FunctionalInterface
	interface CloseCallback {
		/**
		 * Perform an action when a transaction is closed.
		 *
		 * @param transaction The closed transaction. Only {@link #nestingDepth}, {@link #getOpenTransaction} and {@link #addOuterCloseCallback}
		 *                    may be called on that transaction.
		 *                    {@link #addCloseCallback} may additionally be called on parent transactions
		 *                    (accessed through {@link #getOpenTransaction} for lower nesting depths).
		 * @param result The result of this transaction: whether it was committed or aborted.
		 */
		void onClose(Transaction transaction, Result result);
	}

	/**
	 * Register a callback that will be invoked after the outermost transaction is closed,
	 * and after callbacks registered with {@link #addCloseCallback} are ran.
	 * Registered callbacks are invoked last-to-first.
	 *
	 * @throws IllegalStateException If this function is not called on the thread this transaction was opened in.
	 */
	void addOuterCloseCallback(OuterCloseCallback outerCloseCallback);

	/**
	 * A callback that is invoked when the outer transaction is closed.
	 */
	@FunctionalInterface
	interface OuterCloseCallback {
		/**
		 * Perform an action after the top-level transaction is closed.
		 *
		 * @param result The result of the top-level transaction.
		 */
		void afterOuterClose(Result result);
	}

	/**
	 * The result of a transaction operation.
	 */
	enum Result {
		ABORTED,
		COMMITTED;

		/**
		 * @return true if the transaction was aborted, false if it was committed.
		 */
		public boolean wasAborted() {
			return this == ABORTED;
		}

		/**
		 * @return true if the transaction was committed, false if it was aborted.
		 */
		public boolean wasCommitted() {
			return this == COMMITTED;
		}
	}
}
