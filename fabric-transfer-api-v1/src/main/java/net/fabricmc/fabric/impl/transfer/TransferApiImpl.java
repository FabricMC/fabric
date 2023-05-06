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

package net.fabricmc.fabric.impl.transfer;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class TransferApiImpl {
	public static final Logger LOGGER = LoggerFactory.getLogger("fabric-transfer-api-v1");
	public static final AtomicLong version = new AtomicLong();
	@SuppressWarnings("rawtypes")
	public static final Storage EMPTY_STORAGE = new Storage() {
		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public long insert(Object resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(Object resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public Iterator<StorageView> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public long getVersion() {
			return 0;
		}

		@Override
		public String toString() {
			return "EmptyStorage";
		}
	};

	public static <T> Iterator<T> singletonIterator(T it) {
		return new Iterator<T>() {
			boolean hasNext = true;

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public T next() {
				if (!hasNext) {
					throw new NoSuchElementException();
				}

				hasNext = false;
				return it;
			}
		};
	}

	public static <T> Iterator<StorageView<T>> filterEmptyViews(Iterator<StorageView<T>> iterator) {
		return new Iterator<>() {
			StorageView<T> next;

			{
				findNext();
			}

			private void findNext() {
				while (iterator.hasNext()) {
					next = iterator.next();

					if (next.getAmount() > 0 && !next.isResourceBlank()) {
						return;
					}
				}

				next = null;
			}

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public StorageView<T> next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				StorageView<T> ret = next;
				findNext();
				return ret;
			}
		};
	}

	public static <T> List<SingleSlotStorage<T>> makeListView(SlottedStorage<T> storage) {
		return new AbstractList<>() {
			@Override
			public SingleSlotStorage<T> get(int index) {
				return storage.getSlot(index);
			}

			@Override
			public int size() {
				return storage.getSlotCount();
			}
		};
	}
}
