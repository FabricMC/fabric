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

package net.fabricmc.fabric.mixin.transfer;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {
	// Inject after vanilla's attempts to insert the stack into an inventory.
	@Inject(method = "transferOrSpawnStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", shift = At.Shift.BEFORE))
	private void transferOrSpawnStack(ServerWorld world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack inputStack, BlockState state, RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci, @Local Direction direction, @Local Inventory inventory, @Local(ordinal = 1) ItemStack itemStack) {
		if (inventory != null) {
			// Vanilla already found and tested an inventory, nothing else to do even if it failed to insert.
			return;
		}

		if (itemStack.isEmpty()) {
			// Nothing left to do, in theory should never get here.
			return;
		}

		final Storage<ItemVariant> target = ItemStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());

		if (target != null) {
			// Attempt to move the entire stack, and decrement the size of success moves.
			try (Transaction transaction = Transaction.openOuter()) {
				long moved = target.insert(ItemVariant.of(itemStack), inputStack.getCount(), transaction);

				if (moved > 0) {
					itemStack.decrement((int) moved);
					transaction.commit();
				}
			}
		}

		// Any remaining will be dropped in the world by vanilla logic
	}
}
