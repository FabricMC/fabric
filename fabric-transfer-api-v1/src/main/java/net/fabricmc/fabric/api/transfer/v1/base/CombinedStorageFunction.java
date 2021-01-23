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

package net.fabricmc.fabric.api.transfer.v1.base;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

// TODO: check overflow?
public class CombinedStorageFunction<T> implements StorageFunction<T> {
	private final List<StorageFunction<T>> parts;

	public CombinedStorageFunction(List<? extends StorageFunction<T>> parts) {
		this.parts = new ArrayList<>(parts);
	}

	@Override
	public long apply(T resource, long numerator, long denominator, Transaction tx) {
		long total = 0;

		for (StorageFunction<T> part : parts) {
			total += part.apply(resource, numerator - total, denominator, tx);
		}

		return total;
	}

	// TODO: should this be cached in the ctor?
	@Override
	public boolean isEmpty() {
		for (StorageFunction<T> part : parts) {
			if (!part.isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
