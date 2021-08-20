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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;

/**
 * Allows droppers to insert into ItemVariant storages.
 *
 * <p>Maintainer note: it's important that we inject BEFORE the getStack() call,
 * as the returned stack can be mutated by the StorageUtil.move() call in the injected callback.
 */
@Mixin(DropperBlock.class)
public class DropperBlockMixin {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/entity/DispenserBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;"
			),
			method = "dispense",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true,
			allow = 1
	)
	public void hookDispense(ServerWorld world, BlockPos pos, CallbackInfo ci, BlockPointerImpl blockPointerImpl, DispenserBlockEntity dispenser, int slot) {
		if (dispenser.getStack(slot).isEmpty()) return;

		Direction direction = world.getBlockState(pos).get(DispenserBlock.FACING);
		Storage<ItemVariant> target = ItemStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());

		if (target != null) {
			Storage<ItemVariant> source = InventoryStorage.of(dispenser, null).getSlots().get(slot);

			if (StorageUtil.move(source, target, k -> true, 1, null) == 1) {
				ci.cancel();
			}
		}
	}
}
