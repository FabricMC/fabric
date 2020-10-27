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

package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.item.v1.ItemExplosionHandler;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin {
	@Shadow
	public abstract ItemStack getStack();

	/**
	 * Cancels the destruction of an item if the damage source is explosive.
	 * This allows similar logic to the nether star in vanilla.
	 */
	@Inject(method = "damage", at = @At("HEAD"))
	private void handleItemExplosion(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		if (!source.isExplosive()) {
			return; // Don't handle anything that isn't an explosive damage source
		}

		if (this.getStack().isEmpty()) {
			return; // Fallback to vanilla
		}

		final Item item = this.getStack().getItem();
		final ItemExplosionHandler itemExplosionHandler = ((ItemExtensions) item).fabric_getItemExplosionHandler();

		// DamageSource#isExplosive has already been evaluated as true here
		if (itemExplosionHandler != null && !itemExplosionHandler.shouldDestroy((ItemEntity) (Object) this, source, amount)) {
			info.setReturnValue(false);
		}
	}
}
