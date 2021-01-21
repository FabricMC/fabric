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
 * <p>Transactions may only happen on the server thread, and they are global.
 * If a transaction is already open, opening a new transaction will create a nested transaction.
 * <ul>
 *     <li>Transaction state can be viewed as a stack.</li>
 *     <li>Nested transactions can be committed or rolled back like a regular transaction,
 *     but if a transaction is rolled back all its nested transactions will be rolled back as well,
 *     even if they were committed.</li>
 *     <li>In practice, this means that when a participant enlists itself in a transaction,
 *     the transaction manager ensures that it's enlisted in all the transactions in the stack.
 *     It is guaranteed that {@link Participant#onEnlist} will be called for a parent transaction
 *     before it's called for a child transaction.</li>
 *     <li>{@link Participant#onClose} will be called for every closed transaction,
 *     but a committed nested transaction may be aborted later.
 *     As such, it is better to defer irreversible success until {@link Participant#onFinalSuccess} is called,
 *     which will only be called on success for the root transaction.</li>
 * </ul></p>
 */
public interface Transaction extends AutoCloseable {
	/**
	 * Rollback all changes that happened during this transaction.
	 */
	void rollback();

	/**
	 * Validate all changes that happened during this transaction.
	 */
	void commit();

	/**
	 * Rollback, called automatically at the end of try-with-resources.
	 */
	@Override
	void close();

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
	 * If the transaction is not rolled back or committed when it is closed, it will be rolled back.
	 */
	static Transaction open() {
		return TransactionImpl.open();
	}

	/**
	 * Return whether a transaction is currently open.
	 */
	static boolean isOpen() {
		return TransactionImpl.isOpen();
	}
}
