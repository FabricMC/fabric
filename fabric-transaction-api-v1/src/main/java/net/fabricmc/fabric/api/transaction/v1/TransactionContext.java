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

/**
 * Defines the object used to share data between a transaction and a specific participant.<p>
 *
 * Participants should never retain a reference - this instance may be reused for other participants.
 */
public interface TransactionContext {
	/**
	 * Use during {@link TransactionParticipant.TransactionDelegate#prepareRollback(TransactionContext)} to
	 * save rollback state in the transaction.  The state can be any type of instance or {@code null}. It
	 * will never be inspected or altered by the transaction.
	 *
	 * @param <T> Class of the state instance
	 * @param state the rollback state
	 */
	<T> void setState(T state);

	/**
	 * Use during when the consumer returned by {@link TransactionParticipant.TransactionDelegate#prepareRollback(TransactionContext)}
	 * is called to retrieve the rollback state that was earlier passed to {@link #setState(Object)}.
	 *
	 * @param <T> Class of the state instance
	 * @return the rollback state previously passed to {@link #setState(Object)}
	 */
	<T> T getState();

	/**
	 * Use during when the consumer returned by {@link TransactionParticipant.TransactionDelegate#prepareRollback(TransactionContext)}
	 * to test if the transaction is rolled back or committed. If rolled back, the participant must undo
	 * all state changes that happened during the transaction.
	 *
	 * @return {@code true} if the transaction was committed, false if it was rolled back on request or because of an exception
	 */
	boolean isCommited();
}
