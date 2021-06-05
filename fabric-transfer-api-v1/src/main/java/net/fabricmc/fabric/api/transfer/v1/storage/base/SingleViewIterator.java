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

package net.fabricmc.fabric.api.transfer.v1.storage.base;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * An iterator for a single {@link StorageView}, tied to a transaction. Instances can be created with {@link #create}.
 *
 * <p>This class should only be used by implementors of {@link Storage#iterator}, that wish to expose a single storage view.
 * In that case, usage of this class is recommended, as it will ensure that the storage view can't be accessed after the transaction is closed.
 *
 * @param <T> The type of the stored resource.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public final class SingleViewIterator<T> implements Iterator<StorageView<T>>, Transaction.CloseCallback {
	/**
	 * Create a new iterator for the passed storage view, tied to the passed transaction.
	 *
	 * <p>The iterator will ensure that it can only be used as long as the transaction is open.
	 */
	public static <T> Iterator<StorageView<T>> create(StorageView<T> view, Transaction transaction) {
		SingleViewIterator<T> it = new SingleViewIterator<>(view);
		transaction.addCloseCallback(it);
		return it;
	}

	private boolean open = true;
	private boolean hasNext = true;
	private final StorageView<T> view;

	private SingleViewIterator(StorageView<T> view) {
		this.view = view;
	}

	@Override
	public boolean hasNext() {
		return open && hasNext;
	}

	@Override
	public StorageView<T> next() {
		if (!open) {
			throw new NoSuchElementException("The transaction for this iterator was closed.");
		}

		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		hasNext = false;
		return view;
	}

	@Override
	public void onClose(Transaction transaction, Transaction.Result result) {
		open = false;
	}
}
