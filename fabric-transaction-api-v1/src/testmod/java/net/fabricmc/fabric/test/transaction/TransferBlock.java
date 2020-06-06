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

package net.fabricmc.fabric.test.transaction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transaction.v1.Transaction;

public class TransferBlock extends Block {
	public TransferBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos.north());
		if (!(be instanceof Inventory)) return ActionResult.FAIL;
		InventoryTransactionSupport inv = InventoryTransactionSupport.create((Inventory) be);
		CauldronTransactionSupport cauldron = CauldronTransactionSupport.create(world, pos.south());
		if (cauldron == null) return ActionResult.FAIL;

		if (!world.isClient()) {
			Transaction ta = Transaction.create(world);
			int bottles = extract(inv.getCurrentState(ta), new ItemStack(Items.GLASS_BOTTLE, 1)).getCount();

			if (bottles == 1) {
				int water = cauldron.extract(ta, 1);

				if (water == 1) {
					if (HopperBlockEntity.transfer(null, inv.getCurrentState(ta), PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER), null).isEmpty()) {
						ta.commit();
					}
				}
			}
		}

		return ActionResult.SUCCESS;
	}

	public static ItemStack extract(Inventory from, ItemStack stack) {
		ItemStack result = ItemStack.EMPTY;

		for (int i = 0; i < from.size() && !stack.isEmpty(); i++) {
			ItemStack invStack = from.getStack(i);

			if (ItemStack.areItemsEqual(stack, invStack)) {
				int toTake = Math.min(invStack.getCount(), stack.getCount());

				if (result == ItemStack.EMPTY) {
					result = invStack.copy();
					result.setCount(toTake);
				} else {
					result.increment(toTake);
				}

				invStack.decrement(toTake);
				stack.decrement(toTake);
			}
		}

		return result;
	}
}
