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

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;

/**
 * Allows hoppers to interact with ItemVariant storages.
 */
@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@Inject(
			at = @At("HEAD"),
			method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z",
			cancellable = true
	)
	private static void hookInsert(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
		Direction direction = state.get(HopperBlock.FACING);
		BlockPos targetPos = pos.offset(direction);
		BlockEntity targetBe = world.getBlockEntity(targetPos);
		Storage<ItemVariant> target = ItemStorage.SIDED.find(world, targetPos, null, targetBe, direction.getOpposite());

		if (target != null) {
			cir.setReturnValue(doTransfer(InventoryStorage.of(inventory, direction), target, inventory, targetBe));
		}
	}

	@Inject(
			at = @At("HEAD"),
			method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
			cancellable = true
	)
	private static void hookExtract(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
		BlockPos sourcePos = new BlockPos(hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
		BlockEntity sourceBe = world.getBlockEntity(sourcePos);
		Storage<ItemVariant> source = ItemStorage.SIDED.find(world, sourcePos, null, sourceBe, Direction.DOWN);

		if (source != null) {
			cir.setReturnValue(doTransfer(source, InventoryStorage.of(hopper, Direction.UP), sourceBe, hopper));
		}
	}

	private static boolean doTransfer(Storage<ItemVariant> from, Storage<ItemVariant> to, @Nullable Object invFrom, @Nullable Object invTo) {
		if (invFrom instanceof HopperBlockEntityAccessor hopperFrom && invTo instanceof HopperBlockEntityAccessor hopperTo) {
			// Hoppers have some special interactions (see HopperBlockEntity#transfer)
			boolean wasEmpty = hopperTo.isEmpty();
			boolean moved = StorageUtil.move(from, to, k -> true, 1, null) == 1;

			if (moved && wasEmpty && hopperTo.fabric_getLastTickTime() >= hopperFrom.fabric_getLastTickTime()) {
				hopperTo.fabric_callSetCooldown(7);
			}

			return moved;
		} else {
			return StorageUtil.move(from, to, k -> true, 1, null) == 1;
		}
	}
}
