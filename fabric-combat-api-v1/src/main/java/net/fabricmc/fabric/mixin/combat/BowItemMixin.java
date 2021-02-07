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

package net.fabricmc.fabric.mixin.combat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.combat.v1.ShotProjectileEvents;
import net.fabricmc.fabric.api.combat.v1.bow.FabricBowExtensions;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
	@Unique
	private PersistentProjectileEntity shotProjectile;

	// Allows custom bows to modify the projectile shot by bows
	// Two mixins are needed for this in order to capture the locals
	@Inject(method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onStoppedUsing_modifyArrow(ItemStack bowStack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info, PlayerEntity playerEntity, boolean bl, ItemStack arrowStack, int i, float pullProgress, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
		shotProjectile = ShotProjectileEvents.BOW_SHOT_PROJECTILE.invoker().onProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);
	}

	// Actually modifies the projectile
	@ModifyVariable(method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	public PersistentProjectileEntity onStoppedUsing_replaceArrow(PersistentProjectileEntity persistentProjectileEntity) {
		return shotProjectile;
	}

	// Modifies the pull progress if a custom bow is used
	@Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
	private float redirectPullProgress(int useTicks, ItemStack bowStack, World world, LivingEntity user, int remainingUseTicks) {
		if (this instanceof FabricBowExtensions) {
			return ((FabricBowExtensions) this).getCustomPullProgress(useTicks, bowStack);
		}

		return BowItem.getPullProgress(useTicks);
	}
}

