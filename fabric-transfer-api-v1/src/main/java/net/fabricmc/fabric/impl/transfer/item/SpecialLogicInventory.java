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

package net.fabricmc.fabric.impl.transfer.item;

import net.minecraft.item.ItemStack;

/**
 * Internal class that allows inventory instances to defer special logic until {@link InventorySlotWrapper#onFinalCommit()} is called.
 */
public interface SpecialLogicInventory {
	/**
	 * Decide whether special logic should now be suppressed. If true, must remain suppressed until the next call.
	 */
	void fabric_setSuppress(boolean suppress);

	void fabric_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack);
}
