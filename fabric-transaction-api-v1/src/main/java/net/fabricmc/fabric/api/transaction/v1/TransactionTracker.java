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

package net.fabricmc.fabric.api.transaction.v1;

import net.minecraft.world.World;

public class TransactionTracker {
	private static final TransactionTracker CLIENT = new TransactionTracker();
	private static final TransactionTracker SERVER = new TransactionTracker();

	private Transaction current;

	/**
	 * Create a new transaction.
	 *
	 * <p>Calling this will invalidate any existing transactions.
	 *
	 * @return the new transaction
	 */
	public Transaction create() {
		if (this.current != null) {
			this.current.invalidate();
		}

		Transaction ta = new Transaction(null);
		this.current = ta;
		return ta;
	}

	public static TransactionTracker getInstance(World world) {
		return world.isClient() ? CLIENT : SERVER;
	}
}
