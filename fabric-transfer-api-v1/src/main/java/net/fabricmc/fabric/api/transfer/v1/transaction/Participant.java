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

/**
 * An object that can take part atomically in a {@link Transaction}.
 * @see Transaction
 */
public interface Participant<State> {
	/**
	 * Return the state to be put in the Transaction for this participant.
	 * This will be called every time a participant is enlisted for the first time in a transaction.
	 * <p>Note: Returning {@code null} is allowed.</p>
	 */
	State onEnlist();

	/**
	 * This will be called when a transaction is closed if the participant was enlisted.
	 */
	void onClose(State state, TransactionResult result);

	/**
	 * This will be called at the end of the outermost transaction if it is successful, exactly once per participant
	 * involved in the transaction. Block updates should be deferred until then.
	 */
	default void onFinalSuccess() {
	}
}
