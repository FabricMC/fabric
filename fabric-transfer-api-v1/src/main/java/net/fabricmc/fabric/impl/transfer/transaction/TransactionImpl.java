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
import java.util.concurrent.locks.ReentrantLock;

import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionResult;

public class TransactionImpl implements Transaction {
	private static Thread serverThread = null;
	// outer lock: non-server threads contend on this one first.
	private static final ReentrantLock outerLock = new ReentrantLock();
	// inner lock: the server thread directly contends on this one with the non-server thread who owns the outerLock.
	private static final ReentrantLock innerLock = new ReentrantLock();

	private static final ArrayList<TransactionImpl> STACK = new ArrayList<>();
	private static int stackPointer = -1;
	// flag to prevent transaction functions from being called from within a transaction function.
	// for example, to make sure that transaction functions are not called from Participant.onEnlist().
	private static boolean allowAccess = true;

	static {
		STACK.add(new TransactionImpl());
	}

	@SuppressWarnings("rawtypes")
	private final IdentityHashMap<Participant, Object> stateStorage = new IdentityHashMap<>();
	private boolean isOpen = false;

	public static void setServerThread(Thread serverThread) {
		if (innerLock.isLocked()) {
			throw new AssertionError("Something is terribly wrong.");
		}

		TransactionImpl.serverThread = serverThread;
	}

	// validate that Transaction instance methods are valid to call.
	private void validateCurrent() {
		if (!innerLock.isHeldByCurrentThread()) {
			throw new IllegalStateException("Transaction operations are not allowed on this thread.");
		}

		if (!allowAccess) {
			throw new IllegalStateException("Transaction operations are not allowed at the moment.");
		}

		if (STACK.get(stackPointer) != this) {
			throw new IllegalStateException("Transaction operations must be applied to the most recent open transaction.");
		}

		if (!isOpen) {
			throw new IllegalStateException("Transaction operations cannot be applied to a closed transaction.");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void close(TransactionResult result) {
		validateCurrent();
		// block transaction operations
		allowAccess = false;

		// notify participants
		for (Map.Entry<Participant, Object> entry : stateStorage.entrySet()) {
			entry.getKey().onClose(entry.getValue(), result);
		}

		// if root and success, call onFinalSuccess
		if (stackPointer == 0 && result.wasCommitted()) {
			stateStorage.keySet().forEach(Participant::onFinalCommit);
		}

		// clear things up
		stateStorage.clear();
		stackPointer--;
		allowAccess = true;

		// release locks
		if (stackPointer == -1) {
			innerLock.unlock();

			if (Thread.currentThread() != serverThread) {
				outerLock.unlock();
			}
		}
	}

	@Override
	public void abort() {
		close(TransactionResult.ABORTED);
	}

	@Override
	public void commit() {
		close(TransactionResult.COMMITTED);
	}

	@Override
	public void close() {
		if (isOpen()) {
			abort();
		}
	}

	@Override
	public Transaction openNested() {
		validateCurrent();
		stackPointer++;

		if (stackPointer == STACK.size()) {
			STACK.add(new TransactionImpl());
		}

		return STACK.get(stackPointer);
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

	public static Transaction openOuter() {
		if (isOpen()) {
			throw new IllegalStateException("An outer transaction is already active on this thread.");
		}

		// acquire lock
		if (Thread.currentThread() != serverThread) {
			outerLock.lock();
		}

		innerLock.lock();

		// open transaction, STACK always has at least one element.
		stackPointer = 0;
		STACK.get(stackPointer).isOpen = true;
		return STACK.get(stackPointer);
	}

	public static boolean isOpen() {
		return innerLock.isHeldByCurrentThread();
	}
}
