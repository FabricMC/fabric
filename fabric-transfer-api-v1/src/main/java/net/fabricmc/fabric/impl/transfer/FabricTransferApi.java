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

import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class FabricTransferApi {
	public static int version = 0;

	@SuppressWarnings("rawtypes")
	public static final StorageFunction EMPTY = new StorageFunction() {
		@Override
		public long apply(Object resource, long amount, Transaction tx) {
			return 0;
		}

		@Override
		public long apply(Object resource, long numerator, long denominator, Transaction tx) {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	};

	@SuppressWarnings("rawtypes")
	public static final StorageFunction IDENTITY = new StorageFunction() {
		@Override
		public long apply(Object resource, long amount, Transaction tx) {
			return amount;
		}

		@Override
		public long apply(Object resource, long numerator, long denominator, Transaction tx) {
			return numerator;
		}
	};
}
