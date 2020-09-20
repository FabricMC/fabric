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

package net.fabricmc.fabric.impl.extensibility.item.v1;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.extensibility.item.v1.FabricCrossbow;

/**
 * This is the default implementation for FabricCrossbow, allowing for the easy creation of new bows with no new modded functionality.
 */
public class FabricCrossbowItem extends CrossbowItem implements FabricCrossbow {
	public FabricCrossbowItem(Settings settings) {
		super(settings);
	}

	@Override
	public void modifyShotProjectile(ItemStack crossbowStack, LivingEntity entity, ItemStack projectileStack, PersistentProjectileEntity persistentProjectileEntity) {
	}

	@Override
	public float getSpeed(ItemStack stack, LivingEntity entity) {
		return getSpeed(stack);
	}
}
