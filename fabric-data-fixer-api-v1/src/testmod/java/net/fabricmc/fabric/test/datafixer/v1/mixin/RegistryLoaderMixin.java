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

package net.fabricmc.fabric.test.datafixer.v1.mixin;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.TheEndBiomeCreator;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

import net.fabricmc.fabric.test.datafixer.v1.DataFixerTest;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
	@SuppressWarnings({"unchecked", "DataFlowIssue"})
	@Inject(
			method = "load",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
					ordinal = 1,
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void registerTestmodBiome(
			RegistryLoader.RegistryLoadable loadable,
			DynamicRegistryManager baseRegistryManager,
			List<RegistryLoader.Entry<?>> entries,
			CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir,
			Map<RegistryKey<?>, Exception> map,
			List<RegistryLoader.Loader<?>> loaders,
			RegistryOps.RegistryInfoGetter registryInfoGetter
	) {
		Stream<MutableRegistry<?>> registries = loaders.stream().map(RegistryLoader.Loader::registry);
		Map<RegistryKey<?>, MutableRegistry<?>> registryMap = new Object2ObjectOpenHashMap<>();
		registries.forEach(registry -> registryMap.put(registry.getKey(), registry));

		MutableRegistry<Biome> biomeRegistry = (MutableRegistry<Biome>) registryMap.get(RegistryKeys.BIOME);
		MutableRegistry<PlacedFeature> placedFeatureRegistry = (MutableRegistry<PlacedFeature>) registryMap.get(RegistryKeys.PLACED_FEATURE);
		MutableRegistry<ConfiguredCarver<?>> configuredCarverRegistry = (MutableRegistry<ConfiguredCarver<?>>) registryMap.get(RegistryKeys.CONFIGURED_CARVER);

		if (biomeRegistry == null || placedFeatureRegistry == null || configuredCarverRegistry == null || biomeRegistry.contains(DataFixerTest.BIOME_KEY)) {
			return;
		}

		Biome customBiome = TheEndBiomeCreator.createEndHighlands(placedFeatureRegistry.createMutableEntryLookup(), configuredCarverRegistry.createMutableEntryLookup());
		Registry.register(biomeRegistry, DataFixerTest.BIOME_KEY, customBiome);
	}
}