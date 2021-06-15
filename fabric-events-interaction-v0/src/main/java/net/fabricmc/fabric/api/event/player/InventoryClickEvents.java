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

package net.fabricmc.fabric.api.event.player;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Provides events related to clicking in an inventory.
 * Events are called on both sides.
 *
 * @deprecated Experimental feature, may be removed or be incompatibly changed at any time
 */
@ApiStatus.Experimental
@Deprecated
public class InventoryClickEvents {
	/**
	 * Called when the cursor stack clicks on another stack.
	 */
	@ApiStatus.Experimental
	@Deprecated
	public static final Event<StackClicked> STACK_CLICKED = EventFactory.createArrayBacked(StackClicked.class, listeners -> ((itemStack, slot, screenHandler, clickType, player, playerInventory) -> {
		for (StackClicked listener : listeners) {
			ActionResult result = listener.onStackClicked(itemStack, slot, screenHandler, clickType, player, playerInventory);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.SUCCESS;
	}));

	/**
	 * Called when a stack is clicked on by the cursor stack.
	 */
	@ApiStatus.Experimental
	@Deprecated
	public static final Event<Clicked> CLICKED = EventFactory.createArrayBacked(Clicked.class, listeners -> ((itemStack, other, slot, screenHandler, clickType, player, playerInventory) -> {
		for (Clicked listener : listeners) {
			ActionResult result = listener.onClicked(itemStack, other, slot, screenHandler, clickType, player, playerInventory);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.SUCCESS;
	}));

	@ApiStatus.Experimental
	@Deprecated
	@FunctionalInterface
	public interface StackClicked {
		/**
		 * Called when the cursor stack clicks on another stack.
		 * <p>Upon Return:
		 * <ul>
		 *     <li>{@code SUCCESS} cancels further processing and calls {@link Item#onStackClicked(ItemStack, Slot, ClickType, PlayerInventory)}</li>
		 *     <li>{@code PASS} falls back to further processing</li>
		 *     <li>{@code CONSUME} cancels further processing, does not call {@link Item#onStackClicked(ItemStack, Slot, ClickType, PlayerInventory)} and returns {@code true}</li>
		 *     <li>{@code FAIL} cancels further processing and does not call {@link Item#onStackClicked(ItemStack, Slot, ClickType, PlayerInventory)} and returns {@code false}</li>
		 * </ul>
		 * </p>
		 *
		 * @param itemStack The cursor stack
		 * @param slot The slot clicked on
		 * @param screenHandler The current screen handler
		 * @param clickType The type of click
		 * @param player The player who performed the click
		 * @param playerInventory The inventory of the player who performed the click
		 */
		ActionResult onStackClicked(ItemStack itemStack, Slot slot, ScreenHandler screenHandler, ClickType clickType, PlayerEntity player, PlayerInventory playerInventory);
	}

	@ApiStatus.Experimental
	@Deprecated
	@FunctionalInterface
	public interface Clicked {
		/**
		 * Called when the cursor stack clicks on another stack.
		 * <p>Upon Return:
		 * <ul>
		 *     <li>{@code SUCCESS} cancels further processing and calls {@link Item#onClicked(ItemStack, ItemStack, Slot, ClickType, PlayerInventory)}</li>
		 *     <li>{@code PASS} falls back to further processing</li>
		 *     <li>{@code CONSUME} cancels further processing and does not call {@link Item#onClicked(ItemStack, ItemStack, Slot, ClickType, PlayerInventory)} and returns {@code true}</li>
		 *     <li>{@code FAIL} cancels further processing and does not call {@link Item#onClicked(ItemStack, ItemStack, Slot, ClickType, PlayerInventory)} and returns {@code false}</li>
		 * </ul>
		 * </p>
		 * @param itemStack The stack clicked on
		 * @param clickStack The cursor stack, which clicked {@code itemStack}
		 * @param slot The slot clicked on
		 * @param screenHandler The current screen handler
		 * @param clickType The type of click
		 * @param player The player who performed the click
		 * @param playerInventory The inventory of the player who performed the click
		 */
		ActionResult onClicked(ItemStack itemStack, ItemStack clickStack, Slot slot, ScreenHandler screenHandler, ClickType clickType, PlayerEntity player, PlayerInventory playerInventory);
	}
}
