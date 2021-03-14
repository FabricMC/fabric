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

package net.fabricmc.fabric.test.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.RecipeManagerHelper;
import net.fabricmc.fabric.api.recipe.v1.VanillaRecipeBuilders;

public class RecipeApiTestMod implements ModInitializer {
	public static final String NAMESPACE = "fabric-recipe-api-v1-testmod";
	private static final Random RANDOM = new Random();
	private static final List<Item> RANDOM_ITEMS_POOL = Arrays.asList(
			Items.COMMAND_BLOCK,
			Items.COMMAND_BLOCK_MINECART,
			Items.ELYTRA,
			Items.CHAIN_COMMAND_BLOCK,
			Items.REPEATING_COMMAND_BLOCK
	);

	@Override
	public void onInitialize() {
		// Recipe with stick -> diamond
		RecipeManagerHelper.registerStaticRecipe(
				VanillaRecipeBuilders.shapelessRecipe(new ItemStack(Items.DIAMOND))
						.ingredient(Items.STICK)
						.build(new Identifier(NAMESPACE, "test1"), ""));

		RecipeManagerHelper.registerDynamicRecipes(handler -> {
			handler.register(new Identifier(NAMESPACE, "test2"),
					id -> VanillaRecipeBuilders.shapedRecipe(new String[] {"IG", "C#"})
							.ingredient('I', Items.IRON_INGOT)
							.ingredient('G', Items.GOLD_INGOT)
							.ingredient('C', Items.COAL)
							.ingredient('#', Items.CHARCOAL)
							.output(pickRandomStack())
							.build(id, ""));
		});

		RecipeManagerHelper.modifyRecipes(handler -> {
			handler.replace(VanillaRecipeBuilders.shapelessRecipe(new ItemStack(Items.NETHER_STAR))
					.ingredient(Items.ACACIA_PLANKS)
					.build(new Identifier("acacia_button"), ""));
			handler.replace(VanillaRecipeBuilders.shapedRecipe(new String[] {"A", "C"})
					.ingredient('A', ItemTags.PLANKS)
					.ingredient('C', Items.COAL)
					.output(new ItemStack(Items.NETHER_BRICK))
					.build(new Identifier("oak_button"), ""));
		});
	}

	private static ItemStack pickRandomStack() {
		Item item = RANDOM_ITEMS_POOL.get(RANDOM.nextInt(RANDOM_ITEMS_POOL.size()));
		return new ItemStack(item);
	}
}
