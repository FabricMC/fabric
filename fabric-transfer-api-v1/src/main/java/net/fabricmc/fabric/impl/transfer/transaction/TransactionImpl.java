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

package net.fabricmc.fabric.impl.transfer.transaction;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class TransactionImpl implements Transaction {
	private static Thread serverThread = null;

	private static final ArrayList<TransactionImpl> STACK = new ArrayList<>();
	private static int stackPointer = -1;
	private static boolean allowAccess = true;

	@SuppressWarnings("rawtypes")
	private final IdentityHashMap<Participant, Object> stateStorage = new IdentityHashMap<>();
	private boolean isOpen = false;

	private void clear() {
		stateStorage.clear();
		isOpen = false;
	}

	public static void setServerThread(Thread serverThread) {
		TransactionImpl.serverThread = serverThread;
	}

	private static void validateGlobalState() {
		if (!allowAccess) {
			throw new IllegalStateException("Transaction operations are not allowed at the moment.");
		}

		if (Thread.currentThread() != serverThread) {
			throw new IllegalStateException("Transaction operations can only be applied on the server thread.");
		}
	}

	private void validateCurrent() {
		validateGlobalState();

		if (!isOpen) {
			throw new IllegalStateException("Transaction operations cannot be applied to a closed transaction.");
		}

		if (STACK.get(stackPointer) != this) {
			throw new IllegalStateException("Transaction operations must be applied to the most recent open transaction.");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void close(boolean success) {
		validateCurrent();
		// block transaction operations
		allowAccess = false;

		// notify participants
		for (Map.Entry<Participant, Object> entry : stateStorage.entrySet()) {
			entry.getKey().onClose(entry.getValue(), success);
		}

		// if root and success, call onFinalSuccess
		if (stackPointer == 0 && success) {
			stateStorage.keySet().forEach(Participant::onFinalSuccess);
		}

		// clear things up
		clear();
		stackPointer--;
		allowAccess = true;
	}

	@Override
	public void rollback() {
		close(false);
	}

	@Override
	public void commit() {
		close(true);
	}

	@Override
	public void close() {
		if (isOpen) {
			rollback();
		}
	}

	@Override
	public void enlist(Participant<?> participant) {
		validateCurrent();
		allowAccess = false;

		for (int i = 0; i <= stackPointer; ++i) {
			STACK.get(i).stateStorage.computeIfAbsent(participant, Participant::onEnlist);
		}

		allowAccess = true;
	}

	public static Transaction open() {
		validateGlobalState();

		++stackPointer;

		if (stackPointer >= STACK.size()) {
			STACK.add(new TransactionImpl());
		}

		STACK.get(stackPointer).isOpen = true;
		return STACK.get(stackPointer);
	}

	public static boolean isOpen() {
		validateGlobalState();

		return stackPointer != -1;
	}
}
