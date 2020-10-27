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

package net.fabricmc.fabric.mixin.extensibility;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.extensibility.item.v1.trident.TridentInterface;

@Mixin(TridentItem.class)
public class TridentItemMixin {
	// Make the tridentStack for trident entities set to the correct item stack for rendering
	@Inject(method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void editTridentEntity(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info, PlayerEntity playerEntity, int j, TridentEntity tridentEntity) {
		if (stack.getItem() instanceof TridentInterface) {
			((TridentEntityAccessor) tridentEntity).setTridentStack(stack);
		}
	}

	// Modify the Trident Entity before it is spawned in the world
	@ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private Entity editTridentType(Entity entity) {
		TridentEntity trident = (TridentEntity) entity;

		if (!(((TridentEntityAccessor) trident).getTridentStack().getItem() instanceof TridentInterface)) {
			return entity;
		} else {
			return ((TridentInterface) ((TridentEntityAccessor) trident).getTridentStack().getItem()).modifyTridentEntity(trident);
		}
	}
}
