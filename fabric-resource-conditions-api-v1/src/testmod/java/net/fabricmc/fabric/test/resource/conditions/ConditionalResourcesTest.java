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

package net.fabricmc.fabric.test.resource.conditions;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class ConditionalResourcesTest {
	private static final String MOD_ID = "fabric-resource-conditions-api-v1-testmod";

	private static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void conditionalRecipes(TestContext context) {
		ServerRecipeManager manager = context.getWorld().getRecipeManager();

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("not_loaded"))).isPresent()) {
			throw new AssertionError("not_loaded recipe should not have been loaded.");
		}

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("loaded"))).isEmpty()) {
			throw new AssertionError("loaded recipe should have been loaded.");
		}

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("item_tags_populated"))).isEmpty()) {
			throw new AssertionError("item_tags_populated recipe should have been loaded.");
		}

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("tags_populated"))).isEmpty()) {
			throw new AssertionError("tags_populated recipe should have been loaded.");
		}

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("tags_populated_default"))).isEmpty()) {
			throw new AssertionError("tags_populated_default recipe should have been loaded.");
		}

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("tags_not_populated"))).isPresent()) {
			throw new AssertionError("tags_not_populated recipe should not have been loaded.");
		}

		if (manager.get(RegistryKey.of(RegistryKeys.RECIPE, id("features_enabled"))).isEmpty()) {
			throw new AssertionError("features_enabled recipe should have been loaded.");
		}

		long loadedRecipes = manager.values().stream().filter(r -> r.id().getValue().getNamespace().equals(MOD_ID)).count();
		if (loadedRecipes != 5) throw new AssertionError("Unexpected loaded recipe count: " + loadedRecipes);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void conditionalPredicates(TestContext context) {
		// Predicates are internally handled as a kind of loot data,
		// hence the yarn name "loot condition".

		RegistryEntryLookup.RegistryLookup registries = context.getWorld().getServer().getReloadableRegistries().createRegistryLookup();

		if (registries.getOptionalEntry(RegistryKey.of(RegistryKeys.PREDICATE, id("loaded"))).isEmpty()) {
			throw new AssertionError("loaded predicate should have been loaded.");
		}

		if (registries.getOptionalEntry(RegistryKey.of(RegistryKeys.PREDICATE, id("not_loaded"))).isPresent()) {
			throw new AssertionError("not_loaded predicate should not have been loaded.");
		}

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void conditionalLootTables(TestContext context) {
		ReloadableRegistries.Lookup registries = context.getWorld().getServer().getReloadableRegistries();

		if (registries.getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, id("blocks/loaded"))) == LootTable.EMPTY) {
			throw new AssertionError("loaded loot table should have been loaded.");
		}

		if (registries.getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, id("blocks/not_loaded"))) != LootTable.EMPTY) {
			throw new AssertionError("not_loaded loot table should not have been loaded.");
		}

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void conditionalDynamicRegistry(TestContext context) {
		Registry<BannerPattern> registry = context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN);

		if (registry.get(id("loaded")) == null) {
			throw new AssertionError("loaded banner pattern should have been loaded.");
		}

		if (registry.get(id("not_loaded")) != null) {
			throw new AssertionError("not_loaded banner pattern should not have been loaded.");
		}

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void conditionalOverlays(TestContext context) {
		RegistryEntryLookup.RegistryLookup registries = context.getWorld().getServer().getReloadableRegistries().createRegistryLookup();

		if (registries.getOptionalEntry(RegistryKey.of(RegistryKeys.PREDICATE, id("do_overlay"))).isEmpty()) {
			throw new AssertionError("do_overlay predicate should have been overlayed.");
		}

		if (registries.getOptionalEntry(RegistryKey.of(RegistryKeys.PREDICATE, id("dont_overlay"))).isPresent()) {
			throw new AssertionError("dont_overlay predicate should not have been overlayed.");
		}

		context.complete();
	}
}
