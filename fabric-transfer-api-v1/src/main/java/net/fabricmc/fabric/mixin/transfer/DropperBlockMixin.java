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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

/**
 * Allows droppers to insert into ItemVariant storages.
 */
@Mixin(DropperBlock.class)
public class DropperBlockMixin {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/dispenser/DispenserBehavior;dispense(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
			),
			method = "dispense",
			cancellable = true,
			allow = 1
	)
	public void hookDispense(ServerWorld world, BlockState blockState, BlockPos pos, CallbackInfo ci) {
		DispenserBlockEntity dispenser = (DispenserBlockEntity) world.getBlockEntity(pos);
		Direction direction = dispenser.getCachedState().get(DispenserBlock.FACING);

		Storage<ItemVariant> target = ItemStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());

		if (target != null) {
			// Always cancel if a storage is available.
			ci.cancel();

			// We pick a non empty slot. It's not necessarily the same as the one vanilla picked, but that doesn't matter.
			int slot = dispenser.chooseNonEmptySlot(world.random);

			if (slot == -1) {
				TransferApiImpl.LOGGER.warn("Skipping dropper transfer because the empty slot is unexpectedly -1.");
				return;
			}

			StorageUtil.move(
					InventoryStorage.of(dispenser, null).getSlot(slot),
					target,
					k -> true,
					1,
					null
			);
		}
	}
}
