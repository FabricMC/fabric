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

import java.util.List;

import com.google.common.base.Preconditions;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class CombinedStorage<T, S extends Storage<T>> implements Storage<T> {
	public final List<S> parts;

	public CombinedStorage(List<S> parts) {
		this.parts = parts;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(T resource, long maxAmount, Transaction transaction) {
		Preconditions.checkArgument(maxAmount >= 0);
		long amount = 0;

		for (int i = 0; i < parts.size() && amount < maxAmount; ++i) {
			amount += parts.get(i).insert(resource, maxAmount - amount, transaction);
		}

		return amount;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(T resource, long maxAmount, Transaction transaction) {
		Preconditions.checkArgument(maxAmount >= 0);
		long amount = 0;

		for (int i = 0; i < parts.size() && amount < maxAmount; ++i) {
			amount += parts.get(i).extract(resource, maxAmount - amount, transaction);
		}

		return amount;
	}

	@Override
	public boolean forEach(Visitor<T> visitor, Transaction transaction) {
		for (S part : parts) {
			if (part.forEach(visitor, transaction)) {
				return true;
			}
		}

		return false;
	}
}
