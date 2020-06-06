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

package net.fabricmc.fabric.api.transaction.v1.helper;

import net.minecraft.inventory.Inventory;

import net.fabricmc.fabric.api.transaction.v1.TransactionParticipant;

/**
 * Vanilla {@Inventory} with default support for transaction control.
 *
 * <p>The default implementation is naive and allocates on prepare each time. Mods that need
 * performance at scale or that have bespoke {@code Inventory} implementations are encouraged to
 * implement {@link TransactionParticipant} directly.
 */
public interface TransactionalInventory extends Inventory, TransactionParticipant {
	@Override
	default boolean isSelfEnlisting() {
		return false;
	}

	@Override
	default TransactionDelegate getTransactionDelegate() {
		return InventoryHelper.prepareDelegate(this);
	}
}
