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

package net.fabricmc.fabric.impl.transfer.context;

import com.google.common.base.Preconditions;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class StorageContainerItemContext implements ContainerItemContext {
	private final ItemKey boundKey;
	private final Storage<ItemKey> storage;

	public StorageContainerItemContext(ItemKey boundKey, Storage<ItemKey> storage) {
		this.boundKey = boundKey;
		this.storage = storage;
	}

	@Override
	public long getCount(Transaction tx) {
		try (Transaction nested = tx.openNested()) {
			return storage.extractionFunction().apply(boundKey, Integer.MAX_VALUE, nested);
		}
	}

	@Override
	public boolean transform(long count, ItemKey into, Transaction tx) {
		Preconditions.checkArgument(count <= getCount(tx));

		try (Transaction nested = tx.openNested()) {
			if (storage.extractionFunction().apply(boundKey, count, nested) != count) {
				throw new AssertionError("Bad implementation.");
			}

			if (storage.insertionFunction().apply(into, count, nested) == count) {
				nested.commit();
				return true;
			}
		}

		return false;
	}
}
