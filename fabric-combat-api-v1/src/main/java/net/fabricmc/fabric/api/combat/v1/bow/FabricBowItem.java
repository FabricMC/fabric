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

package net.fabricmc.fabric.api.combat.v1.bow;

import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

/**
 * This is the default implementation for {@link FabricBowExtensions}, allowing for the easy creation of new bows with no new modded functionality.
 */
public class FabricBowItem extends BowItem implements FabricBowExtensions {
	public FabricBowItem(Settings settings) {
		super(settings);
	}

	@Override
	public final void modifyProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, @NotNull PersistentProjectileEntity persistentProjectileEntity) {
		if (bowStack.getItem() == this) {
			onProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);
		}
	}

	public void onProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, PersistentProjectileEntity persistentProjectileEntity) {
	}
}
