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

import java.util.function.Predicate;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public final class FilteredStorageFunction<T> implements StorageFunction<T> {
	private final StorageFunction<T> inner;
	private final Predicate<T> filter;

	public FilteredStorageFunction(StorageFunction<T> inner, Predicate<T> filter) {
		this.inner = inner;
		this.filter = filter;
	}

	@Override
	public long apply(T resource, long numerator, Transaction tx) {
		if (filter.test(resource)) {
			return inner.apply(resource, numerator, tx);
		} else {
			return 0;
		}
	}
}
