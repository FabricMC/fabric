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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;

/**
 * Allows hoppers to interact with ItemVariant storages.
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends BlockEntity implements Inventory {
	public HopperBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/block/entity/HopperBlockEntity;getOutputInventory()Lnet/minecraft/inventory/Inventory;"
			),
			method = "insert()Z",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true,
			allow = 1
	)
	private void hookInsert(CallbackInfoReturnable<Boolean> cir, Inventory targetInventory) {
		// Let vanilla handle the transfer if it found an inventory.
		if (targetInventory != null) return;

		// Otherwise inject our transfer logic.
		Direction direction = getCachedState().get(HopperBlock.FACING);
		BlockPos targetPos = pos.offset(direction);
		Storage<ItemVariant> target = ItemStorage.SIDED.find(world, targetPos, direction.getOpposite());

		if (target != null) {
			long moved = StorageUtil.move(
					InventoryStorage.of(this, direction),
					target,
					iv -> true,
					1,
					null
			);
			cir.setReturnValue(moved == 1);
		}
	}

	@Inject(
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputInventory(Lnet/minecraft/block/entity/Hopper;)Lnet/minecraft/inventory/Inventory;"
			),
			method = "extract(Lnet/minecraft/block/entity/Hopper;)Z",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true,
			allow = 1
	)
	private static void hookExtract(Hopper hopper, CallbackInfoReturnable<Boolean> cir, Inventory inputInventory) {
		// Let vanilla handle the transfer if it found an inventory.
		if (inputInventory != null) return;

		// Otherwise inject our transfer logic.
		BlockPos sourcePos = new BlockPos(hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
		Storage<ItemVariant> source = ItemStorage.SIDED.find(hopper.getWorld(), sourcePos, Direction.DOWN);

		if (source != null) {
			long moved = StorageUtil.move(
					source,
					InventoryStorage.of(hopper, Direction.UP),
					iv -> true,
					1,
					null
			);
			cir.setReturnValue(moved == 1);
		}
	}
}
