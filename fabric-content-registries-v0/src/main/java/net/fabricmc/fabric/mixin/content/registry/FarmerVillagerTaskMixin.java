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

package net.fabricmc.fabric.mixin.content.registry;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.registry.VillagerPlantableRegistry;

@Mixin(FarmerVillagerTask.class)
public class FarmerVillagerTaskMixin {
	@Nullable
	@Shadow private BlockPos currentTarget;

	private int fabric_currentInventorySlot = -1;

	@ModifyArg(method = "keepRunning", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SimpleInventory;getStack(I)Lnet/minecraft/item/ItemStack;"), index = 0)
	private int fabric_storeCurrentSlot(int slot) {
		this.fabric_currentInventorySlot = slot;
		return slot;
	}

	@ModifyVariable(method = "keepRunning", at = @At("LOAD"))
	private boolean fabric_useRegistryForPlace(boolean current, ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (current) {
			return true;
		}

		SimpleInventory simpleInventory = villagerEntity.getInventory();
		Item currentItem = simpleInventory.getStack(this.fabric_currentInventorySlot).getItem();

		if (VillagerPlantableRegistry.contains(currentItem)) {
			serverWorld.setBlockState(this.currentTarget, VillagerPlantableRegistry.getPlantState(currentItem), 3);
			return true;
		}

		return false;
	}
}
