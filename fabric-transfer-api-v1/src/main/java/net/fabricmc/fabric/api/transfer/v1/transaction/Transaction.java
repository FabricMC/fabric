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

import net.fabricmc.fabric.impl.transfer.transaction.TransactionImpl;

/**
 * A global operation where participants guarantee atomicity: either the whole operation succeeds,
 * or it is completely aborted and rolled back.
 */
public interface Transaction extends AutoCloseable {
	/**
	 * Open a new outer transaction, blocking while transaction operations are active on other threads.
	 *
	 * @throws IllegalStateException If a transaction is already active on the current thread.
	 */
	static Transaction openOuter() {
		return TransactionImpl.openOuter();
	}

	/**
	 * @return True if a transaction is open on the current thread, and false otherwise.
	 */
	static boolean isOpen() {
		return TransactionImpl.isOpen();
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
		 * @param transaction The closed transaction. Only {@link #nestingDepth} and {@link #getOpenTransaction} may be
		 *                    called on that transaction. {@link #addCloseCallback} may additionally be called on
		 *                    parent transactions (accessed through {@link #getOpenTransaction} for lower nesting depths).
		 * @param result The result of this transaction: whether it was committed or aborted.
		 */
		void onClose(Transaction transaction, Result result);
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
