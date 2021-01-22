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

package net.fabricmc.fabric.api.transfer.v1.storage;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.FabricTransferApi;

/**
 * A function that can be used to either insert or extract a resource, depending on the context.
 */
public interface StorageFunction<T> {
	long apply(T resource, long amount, Transaction tx);
	long apply(T resource, long numerator, long denominator, Transaction tx);

	default boolean isEmpty() {
		return false;
	}

	/**
	 * Return an empty storage function, i.e. one that always returns zero.
	 */
	@SuppressWarnings("unchecked")
	static <T> StorageFunction<T> empty() {
		return FabricTransferApi.EMPTY;
	}

	/**
	 * Return an identity storage function, i.e. one that always returns the passed amount.
	 */
	@SuppressWarnings("unchecked")
	static <T> StorageFunction<T> identity() {
		return FabricTransferApi.IDENTITY;
	}
}
