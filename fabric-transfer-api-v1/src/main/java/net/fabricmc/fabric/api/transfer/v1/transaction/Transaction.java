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

public interface Transaction extends AutoCloseable {
	// May only be called when no outer transaction is active on the current thread.
	static Transaction openOuter() {
		return TransactionImpl.openOuter();
	}

	// May be called at any time.
	static boolean isOpen() {
		return TransactionImpl.isOpen();
	}

	// and the current transaction is open.
	Transaction openNested();

	// May only be called from the current transaction, and if it is open.
	void abort();

	// May only be called from the current transaction, and if it is open.
	void commit();

	// May only be called outside of onClose() handlers.
	@Override
	void close();

	// May be called at any time.
	int nestingDepth();

	// May be called at any time.
	Transaction getOpenTransaction(int nestingDepth);

	// May only be called if the transaction is open.
	void addCloseCallback(CloseCallback closeCallback);

	@FunctionalInterface
	interface CloseCallback {
		void onClose(Transaction transaction, TransactionResult result);
	}
}
