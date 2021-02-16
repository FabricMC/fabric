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

package net.fabricmc.fabric.api.screenhandler.v1;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ClickType;

/**
 * An interface for slots that consume a click action.
 * Slot click actions are called before item click actions, and returning true will neglect to have them called whatsoever.
 */
public interface SlotWithClickAction {
	/**
	 * Called when this slot is clicked, before calling any item methods.
	 * Used to determine whether the click is consumed by the slot.
	 *
	 * @param heldStack the stack the in the player's cursor
	 * @param type the click type, either left or right click
	 * @param inventory the player's inventory, the held stack can be safely mutated
	 * @return whether to consume the click event or not, returning false will have the event processed by items, and if left unconsumed will be processed by the screen handler
	 */
	boolean onClicked(ItemStack heldStack, ClickType type, PlayerInventory inventory);
}
