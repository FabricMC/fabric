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
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class TransactionImpl implements Transaction {
	private static Thread serverThread = null;
	// Outer lock: non-server threads contend on this one first.
	private static final ReentrantLock outerLock = new ReentrantLock();
	// Inner lock: the server thread directly contends on this one with the non-server thread who owns the outerLock.
	private static final ReentrantLock innerLock = new ReentrantLock();
	// The transaction stack, objects are reused so it may contain more objects than the transaction depth would suggest.
	private static final ArrayList<TransactionImpl> STACK = new ArrayList<>();
	// The current transaction depth.
	private static int currentDepth = -1;

	private static void acquireLock() {
		if (Thread.currentThread() != serverThread) outerLock.lock();
		innerLock.lock();
	}

	private static void releaseLock() {
		innerLock.unlock();
		if (Thread.currentThread() != serverThread) outerLock.unlock();
	}

	public static Transaction openOuter() {
		if (isOpen()) {
			throw new IllegalStateException("An outer transaction is already active on this thread.");
		}

		acquireLock();

		// open transaction, STACK always has at least one element.
		currentDepth = 0;
		TransactionImpl current = STACK.get(currentDepth);
		current.isOpen = true;
		return current;
	}

	public static boolean isOpen() {
		return innerLock.isHeldByCurrentThread();
	}

	public static void setServerThread(Thread serverThread) {
		if (innerLock.isLocked()) {
			throw new AssertionError("Trying to change server thread while a transaction is open.");
		}

		TransactionImpl.serverThread = serverThread;
	}

	TransactionImpl(int nestingDepth) {
		this.nestingDepth = nestingDepth;
	}

	private final int nestingDepth;
	private final List<CloseCallback> closeCallbacks = new ArrayList<>();
	private boolean isOpen = false;

	// Validate that this is the correct thread for transaction operations.
	private static void validateCurrentThread() {
		if (!innerLock.isHeldByCurrentThread()) {
			throw new IllegalStateException(String.format(
					"Transaction operations are already ongoing on another thread.\nCurrent thread: %s.\n",
					Thread.currentThread().getName()));
		}
	}

	// Validate that this transaction is the current one, and that this is called on the correct thread.
	private void validateCurrentTransaction() {
		validateCurrentThread();

		if (STACK.get(currentDepth) != this) {
			throw new IllegalStateException("Transaction operation can only be applied to the current transaction.");
		}
	}

	// Validate that this transaction is open.
	private void validateOpen() {
		if (!isOpen) {
			throw new IllegalStateException("Transaction operation cannot be applied to a closed transaction.");
		}
	}

	@Override
	public Transaction openNested() {
		validateCurrentTransaction();
		validateOpen();
		currentDepth++;

		if (currentDepth == STACK.size()) {
			STACK.add(new TransactionImpl(currentDepth));
		}

		TransactionImpl current = STACK.get(currentDepth);
		current.isOpen = true;
		return current;
	}

	private void close(Result result) {
		validateCurrentTransaction();
		validateOpen();
		// block transaction operations
		isOpen = false;

		// invoke callbacks in reverse order
		for (int i = closeCallbacks.size()-1; i >= 0; i--) {
			closeCallbacks.get(i).onClose(this, result);
		}

		closeCallbacks.clear();
		currentDepth--;

		if (currentDepth == -1) {
			releaseLock();
		}
	}

	@Override
	public void abort() {
		close(Result.ABORTED);
	}

	@Override
	public void commit() {
		close(Result.COMMITTED);
	}

	@Override
	public void close() {
		if (isOpen() && isOpen) { // check that a transaction is open on this thread and that this transaction is open.
			abort();
		}
	}

	@Override
	public int nestingDepth() {
		validateCurrentThread();
		return nestingDepth;
	}

	@Override
	public Transaction getOpenTransaction(int nestingDepth) {
		validateCurrentThread();

		if (nestingDepth < 0) {
			throw new IndexOutOfBoundsException("Nesting depth may not be negative.");
		}

		if (nestingDepth > currentDepth) {
			throw new IndexOutOfBoundsException("There is no open transaction for nesting depth " + nestingDepth);
		}

		TransactionImpl transaction = STACK.get(nestingDepth);
		transaction.validateOpen();
		return transaction;
	}

	@Override
	public void addCloseCallback(CloseCallback closeCallback) {
		validateCurrentThread();
		validateOpen();
		closeCallbacks.add(closeCallback);
	}
}
