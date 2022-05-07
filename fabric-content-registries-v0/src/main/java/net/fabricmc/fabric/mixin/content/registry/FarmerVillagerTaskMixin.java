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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.registry.VillagerPlantableRegistry;

@Mixin(FarmerVillagerTask.class)
public class FarmerVillagerTaskMixin {
	@Nullable
	@Shadow private BlockPos currentTarget;

	@Inject(method = "keepRunning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;getInventory()Lnet/minecraft/inventory/SimpleInventory;"))
	private void fabric_useRegistry(ServerWorld serverWorld, VillagerEntity villagerEntity, long l, CallbackInfo ci) {
		SimpleInventory simpleInventory = villagerEntity.getInventory();

		for (int i = 0; i < simpleInventory.size(); ++i) {
			ItemStack itemStack = simpleInventory.getStack(i);

			if (VillagerPlantableRegistry.INSTANCE.contains(itemStack.getItem())) {
				serverWorld.setBlockState(this.currentTarget, VillagerPlantableRegistry.INSTANCE.getPlantState(itemStack.getItem()), 3);
				serverWorld.playSound(null, this.currentTarget.getX(), this.currentTarget.getY(), this.currentTarget.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				itemStack.decrement(1);

				if (itemStack.isEmpty()) {
					simpleInventory.setStack(i, ItemStack.EMPTY);
				}

				break;
			}
		}
	}

	@Redirect(method = "keepRunning", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SimpleInventory;size()I"))
	private int fabric_stopDefaultBehaviour(SimpleInventory inventory) {
		return -1;
	}
}
