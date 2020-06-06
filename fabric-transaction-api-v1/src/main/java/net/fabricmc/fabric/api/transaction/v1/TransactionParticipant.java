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

import java.util.function.Consumer;

/**
 * Implement on objects that can participate in transactions.
 */
@FunctionalInterface
public interface TransactionParticipant {
	/**
	 * Override for implementations that want to enlist lazily.
	 * When true, calls to {@link Transaction#enlist(TransactionParticipant)} will
	 * be ignored and implementation should instead call {@link Transaction#enlistSelf(TransactionParticipant)}
	 * when ready to enlist.
	 *
	 * @return {@code true} for implementations that will self-enlist.
	 */
	default boolean isSelfEnlisting() {
		return false;
	}

	/**
	 * Allows instances that share the same rollback/rollforwar state to share a delegate.
	 * If the same delegate is enlisted more than once, it will only be asked to prepare
	 * rollback the first time.
	 *
	 * @return the transaction delegate for this participant
	 */
	TransactionDelegate getTransactionDelegate();

	@FunctionalInterface
	public interface TransactionDelegate {
		/**
		 * Called on enlisting to signal saving of rollback state or whatever
		 * preparation is appropriate for the participating implementation.
		 * Will be called only once per transaction (including nested transactions).<p>
		 *
		 * Consumer is called for both commit and rollback events just in case some
		 * implementations need to lock or store resources internally during a
		 * transaction and need notification when one ends.
		 *
		 * @param The transaction context - use to store and retrieve transaction state and query status at close
		 * @return The action (as a {@code Consumer}) to be run when the transaction is closed
		 */
		Consumer<TransactionContext> prepareCompletionHandler(TransactionContext context);

		/**
		 * Specialized transaction delegate that does nothing. Use as the delegate
		 * for participants without any internal state to be rolled back.
		 */
		TransactionDelegate IGNORE = c0 -> c1 -> {};
	}
}

