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

import java.util.stream.Collectors;

import com.mojang.serialization.JsonOps;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

public class DefaultResourceConditionsTest {
	private static final String TESTMOD_ID = "fabric-resource-conditions-api-v1-testmod";
	private static final String API_MOD_ID = "fabric-resource-conditions-api-v1";
	private static final String UNKNOWN_MOD_ID = "fabric-tiny-potato-api-v1";
	private static final RegistryKey<? extends Registry<Object>> UNKNOWN_REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(TESTMOD_ID, "unknown_registry"));
	private static final Identifier UNKNOWN_ENTRY_ID = Identifier.of(TESTMOD_ID, "tiny_potato");

	private void expectCondition(TestContext context, String name, ResourceCondition condition, boolean expected) {
		RegistryWrapper.WrapperLookup registryLookup = context.getWorld().getRegistryManager();
		boolean actual = condition.test(new RegistryOps.CachedRegistryInfoGetter(registryLookup));

		if (actual != expected) {
			throw new AssertionError("Test \"%s\" for condition %s failed; expected %s, got %s".formatted(name, condition.getType().id(), expected, actual));
		}

		// Test serialization
		ResourceCondition.CODEC.encodeStart(JsonOps.INSTANCE, condition).getOrThrow(message -> new AssertionError("Could not serialize \"%s\": %s".formatted(name, message)));
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void logics(TestContext context) {
		ResourceCondition alwaysTrue = ResourceConditions.alwaysTrue();
		ResourceCondition alwaysFalse = ResourceConditions.not(alwaysTrue);
		ResourceCondition trueAndTrue = ResourceConditions.and(alwaysTrue, alwaysTrue);
		ResourceCondition trueAndFalse = ResourceConditions.and(alwaysTrue, alwaysFalse);
		ResourceCondition emptyAnd = ResourceConditions.and();
		ResourceCondition trueOrFalse = ResourceConditions.or(alwaysTrue, alwaysFalse);
		ResourceCondition falseOrFalse = ResourceConditions.or(alwaysFalse, alwaysFalse);
		ResourceCondition emptyOr = ResourceConditions.or();

		expectCondition(context, "always true", alwaysTrue, true);
		expectCondition(context, "always false", alwaysFalse, false);
		expectCondition(context, "true and true", trueAndTrue, true);
		expectCondition(context, "true and false", trueAndFalse, false);
		expectCondition(context, "vacuous truth", emptyAnd, true);
		expectCondition(context, "true or false", trueOrFalse, true);
		expectCondition(context, "false or false", falseOrFalse, false);
		expectCondition(context, "empty OR is always false", emptyOr, false);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void allModsLoaded(TestContext context) {
		ResourceCondition testmod = ResourceConditions.allModsLoaded(TESTMOD_ID);
		ResourceCondition testmodAndApi = ResourceConditions.allModsLoaded(TESTMOD_ID, API_MOD_ID);
		ResourceCondition unknownMod = ResourceConditions.allModsLoaded(UNKNOWN_MOD_ID);
		ResourceCondition unknownAndTestmod = ResourceConditions.allModsLoaded(UNKNOWN_MOD_ID, TESTMOD_ID);
		ResourceCondition noMod = ResourceConditions.allModsLoaded();

		expectCondition(context, "one loaded mod", testmod, true);
		expectCondition(context, "two loaded mods", testmodAndApi, true);
		expectCondition(context, "one unloaded mod", unknownMod, false);
		expectCondition(context, "both loaded and unloaded mods", unknownAndTestmod, false);
		expectCondition(context, "no mod", noMod, true);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void anyModsLoaded(TestContext context) {
		ResourceCondition testmod = ResourceConditions.anyModsLoaded(TESTMOD_ID);
		ResourceCondition testmodAndApi = ResourceConditions.anyModsLoaded(TESTMOD_ID, API_MOD_ID);
		ResourceCondition unknownMod = ResourceConditions.anyModsLoaded(UNKNOWN_MOD_ID);
		ResourceCondition unknownAndTestmod = ResourceConditions.anyModsLoaded(UNKNOWN_MOD_ID, TESTMOD_ID);
		ResourceCondition noMod = ResourceConditions.anyModsLoaded();

		expectCondition(context, "one loaded mod", testmod, true);
		expectCondition(context, "two loaded mods", testmodAndApi, true);
		expectCondition(context, "one unloaded mod", unknownMod, false);
		expectCondition(context, "both loaded and unloaded mods", unknownAndTestmod, true);
		expectCondition(context, "no mod", noMod, false);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void featuresEnabled(TestContext context) {
		ResourceCondition vanilla = ResourceConditions.featuresEnabled(FeatureFlags.VANILLA);
		// Reminder: GameTest enables all features by default
		ResourceCondition vanillaAndRedstoneExperiments = ResourceConditions.featuresEnabled(FeatureFlags.VANILLA, FeatureFlags.REDSTONE_EXPERIMENTS);
		Identifier unknownId = Identifier.of(TESTMOD_ID, "unknown_feature_to_test_condition");
		ResourceCondition unknown = ResourceConditions.featuresEnabled(unknownId);
		// Passing an array to avoid type ambiguity
		ResourceCondition empty = ResourceConditions.featuresEnabled(new FeatureFlag[]{});

		expectCondition(context, "vanilla only", vanilla, true);
		expectCondition(context, "vanilla and redstone experiments", vanillaAndRedstoneExperiments, true);
		expectCondition(context, "unknown feature ID", unknown, false);
		expectCondition(context, "no feature", empty, true);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void registryContains(TestContext context) {
		// Static registry
		RegistryKey<Block> dirtKey = Registries.BLOCK.getKey(Blocks.DIRT).orElseThrow();
		ResourceCondition dirt = ResourceConditions.registryContains(dirtKey);
		ResourceCondition dirtAndUnknownBlock = ResourceConditions.registryContains(dirtKey, RegistryKey.of(RegistryKeys.BLOCK, UNKNOWN_ENTRY_ID));
		ResourceCondition emptyBlock = ResourceConditions.registryContains(RegistryKeys.BLOCK, new Identifier[]{});
		ResourceCondition unknownRegistry = ResourceConditions.registryContains(UNKNOWN_REGISTRY_KEY, UNKNOWN_ENTRY_ID);
		ResourceCondition emptyUnknown = ResourceConditions.registryContains(UNKNOWN_REGISTRY_KEY, new Identifier[]{});

		expectCondition(context, "dirt", dirt, true);
		expectCondition(context, "dirt and unknown block", dirtAndUnknownBlock, false);
		expectCondition(context, "block registry, empty check", emptyBlock, true);
		expectCondition(context, "unknown registry, non-empty", unknownRegistry, false);
		expectCondition(context, "unknown registry, empty", emptyUnknown, true);

		// Dynamic registry (in vitro; separate testmod needs to determine if this actually functions while loading)
		ResourceCondition plains = ResourceConditions.registryContains(BiomeKeys.PLAINS);
		ResourceCondition unknownBiome = ResourceConditions.registryContains(RegistryKey.of(RegistryKeys.BIOME, UNKNOWN_ENTRY_ID));
		ResourceCondition emptyDynamic = ResourceConditions.registryContains(RegistryKeys.BIOME, new Identifier[]{});

		expectCondition(context, "plains", plains, true);
		expectCondition(context, "unknown biome", unknownBiome, false);
		expectCondition(context, "biome registry, empty check", emptyDynamic, true);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void tagsPopulated(TestContext context) {
		// We need to set the tags ourselves as it is cleared outside the resource loading context.
		ResourceConditionsImpl.LOADED_TAGS.set(
				context.getWorld().getRegistryManager().streamAllRegistries().collect(Collectors.toMap(
						DynamicRegistryManager.Entry::key,
						e -> e.value().streamTags().map(t -> t.getTag().id()).collect(Collectors.toUnmodifiableSet())
				))
		);

		// Static registry
		ResourceCondition dirt = ResourceConditions.tagsPopulated(RegistryKeys.BLOCK, BlockTags.DIRT);
		ResourceCondition dirtAndUnknownBlock = ResourceConditions.tagsPopulated(RegistryKeys.BLOCK, BlockTags.DIRT, TagKey.of(RegistryKeys.BLOCK, UNKNOWN_ENTRY_ID));
		ResourceCondition emptyBlock = ResourceConditions.tagsPopulated(RegistryKeys.BLOCK);
		ResourceCondition unknownRegistry = ResourceConditions.tagsPopulated(UNKNOWN_REGISTRY_KEY, TagKey.of(UNKNOWN_REGISTRY_KEY, UNKNOWN_ENTRY_ID));
		ResourceCondition emptyUnknown = ResourceConditions.tagsPopulated(UNKNOWN_REGISTRY_KEY);

		expectCondition(context, "dirt tag", dirt, true);
		expectCondition(context, "dirt tag and unknown tag", dirtAndUnknownBlock, false);
		expectCondition(context, "block registry, empty tag checks", emptyBlock, true);
		expectCondition(context, "unknown registry, non-empty tag checks", unknownRegistry, false);
		expectCondition(context, "unknown registry, empty tag checks", emptyUnknown, true);

		// Dynamic registry (in vitro; separate testmod needs to determine if this actually functions while loading)
		ResourceCondition forest = ResourceConditions.tagsPopulated(RegistryKeys.BIOME, BiomeTags.IS_FOREST);
		ResourceCondition unknownBiome = ResourceConditions.tagsPopulated(RegistryKeys.BIOME, TagKey.of(RegistryKeys.BIOME, UNKNOWN_ENTRY_ID));
		ResourceCondition emptyDynamic = ResourceConditions.tagsPopulated(RegistryKeys.BIOME);

		expectCondition(context, "forest tag", forest, true);
		expectCondition(context, "unknown biome tag", unknownBiome, false);
		expectCondition(context, "biome registry, empty tag check", emptyDynamic, true);

		context.complete();
	}
}
