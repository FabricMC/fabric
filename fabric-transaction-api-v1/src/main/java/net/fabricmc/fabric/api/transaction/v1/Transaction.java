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

package net.fabricmc.fabric.api.transaction.v1;

import net.fabricmc.fabric.impl.transaction.TransactionImpl;

/**
 * Represents a aggregate operation involving one or
 * more participants in which participants guarantee the
 * entire operation will be atomic.
 *
 * <p>Changes in participant state are immediately effective and
 * visible as they happen, but all participants will roll back
 * all changes that happened after this transaction started if the
 * transaction is closed without calling {@link #commit()}.
 */
public interface Transaction extends AutoCloseable {
	/**
	 * Close the transaction and notify all participants to reverse all state changes
	 * that happened after this transaction was opened.
	 */
	void rollback();

	/**
	 * Close the transaction and notify all participants to retain all state changes
	 * that happened after this transaction was opened.
	 */
	void commit();

	/**
	 * Add the participant to this transaction if it is not already enlisted and
	 * is not self-enrolling.
	 *
	 * @param <T> identifies the specific type of the participant
	 * @param participant the participant to be enrolled if appropriate
	 * @return the participant
	 */
	default <T extends TransactionParticipant> T enlist(T participant) {
		if(!participant.isSelfEnlisting()) {
			return enlistSelf(participant);
		} else {
			return participant;
		}
	}

	/**
	 * Add the participant to this transaction if it is not already enlisted.
	 *
	 * <p>Should only be called by participants that are self-enrolling!
	 *
	 * @param <T> identifies the specific type of the participant
	 * @param participant  the participant to be enrolled
	 * @return the participant to be enrolled if appropriate
	 */
	<T extends TransactionParticipant> T enlistSelf(T participant);

	/**
	 * Closes this transaction as if {@link #rollback()} were called,
	 * unless {@link #commit()} was called first. Has no effect if the
	 * transaction is already closed.
	 */
	@Override
	void close();

	/**
	 * Creates a new transaction.  Should always be called from a try-with-resource block.
	 *
	 * <p>If a transaction is already open, the behavior of this call varies depending on the thread:
	 *
	 * <p>If called from the same thread that opened the current transaction, the new transaction
	 * will be "nested" or enclosed in the existing transaction.  A nested transaction will
	 * be rolled back if the enclosing transaction is rolled back, even if the nested transaction
	 * was successfully committed.
	 *
	 * <p>When a nested transaction is closed, the enclosing transaction will again become the "current"
	 * transaction and if the nested transaction was rolled back the state of the enclosing
	 * transaction will be the same as it was prior to opening the nested transaction.
	 *
	 * <p>If called from a different thread than the current transaction, this method will block
	 * until all current transactions on the other thread are closed, and then return a new,
	 * root-level transaction.
	 *
	 * @return a new transaction
	 */
	static Transaction open() {
		return TransactionImpl.open();
	}

	/**
	 * Retrieves the current open transaction at the deepest level of nesting, or null if
	 * no transaction is currently open.
	 *
	 * @return the current open transaction
	 */
	static /*Nullable*/ Transaction current() {
		return TransactionImpl.current();
	}

	/**
	 * Enlists the given participant in the current open transaction if there is one,
	 * or does nothing if no transaction is open.  If the participant is already
	 * enlisted or is self-enlisting, this has no effect even when a transaction is open.
	 *
	 * @param participant the participant to be enrolled
	 */
	static void enlistIfOpen(TransactionParticipant participant) {
		final Transaction tx = current();

		if(tx != null) {
			tx.enlist(participant);
		}
	}

	/**
	 * Self-enlists the given participant in the current open transaction if there is one,
	 * or does nothing if no transaction is open.  If the participant is already
	 * enlisted, this has no effect even when a transaction is open. Use for self-enlisting implementations.
	 *
	 * @param participant the participant to be enrolled
	 */
	static void selfEnlistIfOpen(TransactionParticipant participant) {
		final Transaction tx = current();

		if(tx != null) {
			tx.enlistSelf(participant);
		}
	}
}
