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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.combat.v1.ShotProjectileEvents;

// Will need to be updated if more bow-attacking mobs are added
@Mixin({AbstractSkeletonEntity.class, IllusionerEntity.class})
public abstract class BowAttackMixin extends MobEntity implements RangedAttackMob {
	protected BowAttackMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	public boolean modifyShotProjectile(World world, Entity persistentProjectileEntity, LivingEntity target, float pullProgress) {
		ItemStack bowStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
		ItemStack arrowStack = this.getArrowType(bowStack);

		PersistentProjectileEntity replacedPersistentProjectileEntity = ShotProjectileEvents.BOW_REPLACE_SHOT_PROJECTILE.invoker().replaceProjectileShot(bowStack, arrowStack, this, pullProgress, (PersistentProjectileEntity) persistentProjectileEntity);
		ShotProjectileEvents.BOW_MODIFY_SHOT_PROJECTILE.invoker().modifyProjectileShot(bowStack, arrowStack, this, pullProgress, replacedPersistentProjectileEntity);

		return world.spawnEntity(replacedPersistentProjectileEntity);
	}
}
