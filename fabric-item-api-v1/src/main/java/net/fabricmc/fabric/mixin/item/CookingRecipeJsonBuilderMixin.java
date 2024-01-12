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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.item.Item;

@Mixin(CookingRecipeJsonBuilder.class)
class CookingRecipeJsonBuilderMixin {
	@WrapOperation(method = "getSmeltingRecipeCategory", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
	private static boolean isStackAwareFood(Item instance, Operation<Boolean> original) {
		return instance.getDefaultStack().isFood();
	}
}
