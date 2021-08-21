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

package net.fabricmc.fabric.test.transfer.unittests;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

class TransactionExceptionsTests {
	public static void run() {
		testTransactionExceptions();
	}

	private static int callbacksInvoked = 0;

	/**
	 * Make sure that transaction global state stays valid in case of exceptions.
	 */
	private static void testTransactionExceptions() {
		// Test exception inside the try.
		ensureException(() -> {
			try (Transaction tx = Transaction.openOuter()) {
				tx.addCloseCallback((t, result) -> {
					callbacksInvoked++; throw new RuntimeException("Close.");
				});
				throw new RuntimeException("Inside try.");
			}
		}, "Exception should have propagated through the transaction.");
		if (callbacksInvoked != 1) throw new AssertionError("Callback should have been invoked.");

		// Test exception inside the close.
		callbacksInvoked = 0;
		ensureException(() -> {
			try (Transaction tx = Transaction.openOuter()) {
				tx.addCloseCallback((t, result) -> {
					callbacksInvoked++; throw new RuntimeException("Close 1.");
				});
				tx.addCloseCallback((t, result) -> {
					callbacksInvoked++; throw new RuntimeException("Close 2.");
				});
				tx.addOuterCloseCallback(result -> {
					callbacksInvoked++; throw new RuntimeException("Outer close 1.");
				});
				tx.addOuterCloseCallback(result -> {
					callbacksInvoked++; throw new RuntimeException("Outer close 2.");
				});
			}
		}, "Exceptions in close callbacks should be propagated through the transaction.");
		if (callbacksInvoked != 4) throw new AssertionError("All 4 callbacks should have been invoked, only so many were: " + callbacksInvoked);

		// Test that transaction state is still OK after these exceptions.
		try (Transaction tx = Transaction.openOuter()) {
			tx.commit();
		}
	}

	private static void ensureException(Runnable runnable, String message) {
		boolean failed = false;

		try {
			runnable.run();
		} catch (Throwable t) {
			failed = true;
		}

		if (!failed) {
			throw new AssertionError(message);
		}
	}
}
