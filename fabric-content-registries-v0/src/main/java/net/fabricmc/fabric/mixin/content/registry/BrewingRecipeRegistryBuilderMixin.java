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

package net.fabricmc.fabric.mixin.content.registry;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;

@Mixin(BrewingRecipeRegistry.Builder.class)
public abstract class BrewingRecipeRegistryBuilderMixin implements FabricBrewingRecipeRegistryBuilder {
	@Shadow
	@Final
	private FeatureSet enabledFeatures;

	@Shadow
	private static void assertPotion(Item potionType) {
	}

	@Shadow
	@Final
	private List<BrewingRecipeRegistry.Recipe<Item>> itemRecipes;

	@Shadow
	@Final
	private List<BrewingRecipeRegistry.Recipe<Potion>> potionRecipes;

	@Inject(method = "build", at = @At("HEAD"))
	private void build(CallbackInfoReturnable<BrewingRecipeRegistry> cir) {
		FabricBrewingRecipeRegistryBuilder.BUILD.invoker().build((BrewingRecipeRegistry.Builder) (Object) this);
	}

	@Override
	public void registerItemRecipe(Item input, Ingredient ingredient, Item output, FeatureFlag... requiredFeatures) {
		if (input.isEnabled(this.enabledFeatures) && FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures).isSubsetOf(this.enabledFeatures) && output.isEnabled(this.enabledFeatures)) {
			assertPotion(input);
			assertPotion(output);
			this.itemRecipes.add(new BrewingRecipeRegistry.Recipe<>(input.getRegistryEntry(), ingredient, output.getRegistryEntry()));
		}
	}

	@Override
	public void registerPotionRecipe(RegistryEntry<Potion> input, Ingredient ingredient, RegistryEntry<Potion> output, FeatureFlag... requiredFeatures) {
		if (input.value().isEnabled(this.enabledFeatures) && FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures).isSubsetOf(this.enabledFeatures) && output.value().isEnabled(this.enabledFeatures)) {
			this.potionRecipes.add(new BrewingRecipeRegistry.Recipe<>(input, ingredient, output));
		}
	}

	@Override
	public void registerRecipes(Ingredient ingredient, RegistryEntry<Potion> potion, FeatureFlag... requiredFeatures) {
		if (potion.value().isEnabled(this.enabledFeatures)) {
			this.registerPotionRecipe(Potions.WATER, ingredient, Potions.MUNDANE, requiredFeatures);
			this.registerPotionRecipe(Potions.AWKWARD, ingredient, potion, requiredFeatures);
		}
	}
}
