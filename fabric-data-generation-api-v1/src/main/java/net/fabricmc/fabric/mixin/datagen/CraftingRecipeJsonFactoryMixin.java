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

package net.fabricmc.fabric.mixin.datagen;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.data.server.recipe.CraftingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

@Mixin(CraftingRecipeJsonFactory.class)
public interface CraftingRecipeJsonFactoryMixin {
	@Shadow
	static Identifier getItemId(ItemConvertible item) {
		throw new AssertionError();
	}

	@Shadow
	Item getOutputItem();

	@Shadow
	void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId);

	@Inject(method = "offerTo(Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
	default void offerTo(Consumer<RecipeJsonProvider> exporter, CallbackInfo info) {
		if (FabricDataGenHelper.processingModId != null) {
			this.offerTo(exporter, new Identifier(FabricDataGenHelper.processingModId, getItemId(this.getOutputItem()).getPath()));
			info.cancel();
		}
	}
}
