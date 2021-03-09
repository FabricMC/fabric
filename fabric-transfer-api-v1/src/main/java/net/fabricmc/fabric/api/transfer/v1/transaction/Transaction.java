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
 * A global operation where {@linkplain Participant participants} guarantee atomicity:
 * either the whole operation succeeds, or it is completely cancelled.
 * In that case, we say that it is aborted.
 *
 * <p>It is possible to open a transaction when anoter transaction is active, using {@link #openNested}.
 * In that case, we say that the new transaction is nested in the outer transaction.
 * Transaction state can be viewed as a stack.
 * Nested transactions can be committed or rolled back like a regular transaction,
 * but if a transaction is rolled back all its nested transactions will be rolled back as well,
 * even if they were committed
 *
 * <p>In practice, this means that when a participant enlists itself in a transaction,
 * the transaction manager ensures that it's enlisted in all the transactions in the stack.
 * It is guaranteed that {@link Participant#onEnlist} will be called for a parent transaction
 * before it's called for a child transaction.
 *
 * <p>{@link Participant#onClose} will be called for every closed transaction,
 * but a committed nested transaction may be aborted later.
 * As such, it is better to defer irreversible success until {@link Participant#onFinalCommit} is called,
 * which will only be called on success for the root transaction.
 *
 * <p>Only one outermost transaction can be open at any given time.
 * Attempts to open a new outer transaction from the thread of the active transaction will throw a RuntimeException.
 * Attempts to open a new outer transaction from another thread will block until the active transaction is closed.
 * The server thread is scheduled before the other threads when possible.
 * Still, try to keep the transaction work on the other threads minimal.
 */
public interface Transaction extends AutoCloseable {
	/**
	 * Abort all changes that happened during this transaction.
	 */
	void abort();

	/**
	 * Validate all changes that happened during this transaction.
	 */
	void commit();

	/**
	 * Abort if open, called automatically at the end of try-with-resources.
	 */
	@Override
	void close();

	/**
	 * Open a nested transaction.
	 */
	Transaction openNested();

	/**
	 * Enlist the participant in the current transaction and all its parents if there is an open transaction,
	 * and if the participant isn't yet enlisted.
	 * {@link Participant#onEnlist} will be called for every transaction in the transaction stack in which it is not enlisted yet,
	 * from the oldest transaction to the most recent one.
	 */
	void enlist(Participant<?> participant);

	/**
	 * Open a new transaction.
	 * It must always be used in a try-with-resources block.
	 * If the transaction is not aborted or committed when it is closed, it will be rolled aborted.
	 */
	static Transaction openOuter() {
		return TransactionImpl.openOuter();
	}

	/**
	 * Return whether a transaction is currently open <b>in the current thread</b>.
	 */
	static boolean isOpen() {
		return TransactionImpl.isOpen();
	}
}
