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

package net.fabricmc.fabric.api.fluid.v1;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * Implements the basic behaviour of every fluid, plus some basic extended behaviour, by implementing ExtendedFlowableFluid.
 */
public abstract class ExtendedFabricFlowableFluid extends FabricFlowableFluid implements ExtendedFlowableFluid {
	/**
	 * Initializes a new ExtendedFabricFlowableFluid.
	 */
	public ExtendedFabricFlowableFluid() {}

	/**
	 * Event executed when the entity is into the fluid.
	 *
	 * @param world  The current world.
	 * @param entity The current entity in the fluid.
	 */
	@Override
	public void onSubmerged(World world, Entity entity) {
		//Implements drowning living entities
		if (!world.isClient && entity instanceof LivingEntity life) {
			float drowningDamage = getDrowningDamage();
			if (drowningDamage > 0 && !life.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(life)) {
				if (!(life instanceof PlayerEntity player && player.getAbilities().invulnerable)) {
					life.setAir(life.getAir() - 1);
					if (life.getAir() <= -20) {
						life.setAir(0);
						life.damage(DamageSource.DROWN, drowningDamage);
					}
				}
			}
		}
	}

	/**
	 * Event executed when the entity is touching the fluid.
	 *
	 * @param world  The current world.
	 * @param entity The current entity in the fluid.
	 */
	@Override
	public void onTouching(World world, Entity entity) {
		//Implements fire and hot damage on entities
		if (!world.isClient && !entity.isFireImmune()) {
			int entityOnFireDuration = getEntityOnFireDuration();
			float hotDamage = getHotDamage();
			if (canLightFire() && entityOnFireDuration > 0) {
				entity.setOnFireFor(entityOnFireDuration);
			}
			if (hotDamage > 0 && entity.damage(DamageSource.IN_FIRE, hotDamage)) {
				entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + world.getRandom().nextFloat() * 0.4F);
			}
		}
	}
}
