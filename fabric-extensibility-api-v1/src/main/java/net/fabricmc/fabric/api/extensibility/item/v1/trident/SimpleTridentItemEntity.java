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

package net.fabricmc.fabric.api.extensibility.item.v1.trident;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.extensibility.TridentEntityAccessor;

/**
 * This is the default implementation for the FabricTridentEntity, allowing for the easy creation of new Trident Entities in Fabric.
 */
public class SimpleTridentItemEntity extends TridentEntity {
	public SimpleTridentItemEntity(EntityType<? extends TridentEntity> entityType, World world, ItemStack tridentStack) {
		super(entityType, world);
		((TridentEntityAccessor) this).setTridentStack(tridentStack);
	}

	public SimpleTridentItemEntity(World world, LivingEntity owner, ItemStack stack) {
		super(world, owner, stack);
		((TridentEntityAccessor) this).setTridentStack(stack);
	}

	@Environment(EnvType.CLIENT)
	public SimpleTridentItemEntity(World world, double x, double y, double z, ItemStack tridentStack) {
		super(world, x, y, z);
		((TridentEntityAccessor) this).setTridentStack(tridentStack);
	}

	public SimpleTridentItemEntity(TridentEntity trident) {
		this(trident.getEntityWorld(), (LivingEntity) trident.getOwner(), ((TridentEntityAccessor) trident).getTridentStack());
		this.setVelocity(trident.getVelocity());
		this.setUuid(trident.getUuid());
		this.pickupType = trident.pickupType;
	}
}
