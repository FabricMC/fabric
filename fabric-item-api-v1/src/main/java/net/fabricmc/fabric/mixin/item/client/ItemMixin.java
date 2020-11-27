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

package net.fabricmc.fabric.mixin.item.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.client.item.v1.UpdateAnimationHandler;
import net.fabricmc.fabric.impl.client.ItemUpdateAnimationHandlerExtensions;

@Mixin(Item.class)
abstract class ItemMixin implements ItemUpdateAnimationHandlerExtensions {
	@Unique
	private UpdateAnimationHandler fabric_updateAnimationHandler = null;

	@Override
	public UpdateAnimationHandler fabric_getUpdateAnimationHandler() {
		return fabric_updateAnimationHandler;
	}

	@Override
	public void fabric_setUpdateAnimationHandler(UpdateAnimationHandler handler) {
		this.fabric_updateAnimationHandler = handler;
	}
}
