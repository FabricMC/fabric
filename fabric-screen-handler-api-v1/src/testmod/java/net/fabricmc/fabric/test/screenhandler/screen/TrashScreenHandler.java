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

package net.fabricmc.fabric.test.screenhandler.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

import net.fabricmc.fabric.api.screenhandler.v1.SlotWithClickAction;
import net.fabricmc.fabric.test.screenhandler.ScreenHandlerTest;

public class TrashScreenHandler extends ScreenHandler {
	public TrashScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerTest.TRASH_SCREEN_HANDLER, syncId);
		this.addSlot(new TrashSlot(new SimpleInventory(1), 0, 80, 20));

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 51 + y * 18));
			}

			this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 109));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	class TrashSlot extends Slot implements SlotWithClickAction {
		TrashSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean onClicked(ItemStack heldStack, ClickType type, PlayerInventory inventory) {
			inventory.setCursorStack(ItemStack.EMPTY);
			return true;
		}
	}
}
