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

package net.fabricmc.fabric.mixin.generatortype;

import java.util.Optional;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import net.fabricmc.fabric.api.generatortype.v1.FabricGeneratorConfig;
import net.fabricmc.fabric.impl.generatortype.FabricGeneratorTypeImpl;

@Mixin(GeneratorOptions.class)
public final class GeneratorOptionsMixin {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "fromProperties", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/dimension/DimensionType;createDefaultDimensionOptions(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;J)Lnet/minecraft/util/registry/SimpleRegistry;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void addSupportForFabricGeneratorTypes(DynamicRegistryManager registryManager, Properties properties, CallbackInfoReturnable<GeneratorOptions> cir, String generatorSettings, String levelSeed, boolean generateStructures, String levelTypeCaseSensitive, String levelType, long seed, Registry<DimensionType> dimensionTypeRegistry, Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> generatorSettingsRegistry, SimpleRegistry<DimensionOptions> dimensionOptionsRegistry) {
		FabricGeneratorTypeImpl<? extends FabricGeneratorConfig> generatorType = FabricGeneratorTypeImpl.GENERATOR_TYPE_MAP.get(levelType);

		if (generatorType != null) {
			JsonObject jsonObject = !generatorSettings.isEmpty() ? JsonHelper.deserialize(generatorSettings) : new JsonObject();
			FabricGeneratorConfig generatorConfig = generatorType.getGeneratorConfig(biomeRegistry);

			if (generatorConfig != null) {
				DataResult<? extends FabricGeneratorConfig> dataResult = generatorConfig.getCodec().parse(new Dynamic<>(JsonOps.INSTANCE, jsonObject));
				Optional<? extends FabricGeneratorConfig> config = dataResult.resultOrPartial(LOGGER::error);
				generatorConfig = config.isPresent() ? config.get() : generatorConfig;
			}

			cir.setReturnValue(new GeneratorOptions(seed, generateStructures, false, GeneratorOptions.method_28608(dimensionTypeRegistry, dimensionOptionsRegistry,
					generatorType.getChunkGenerator(generatorType.getBiomeSource(biomeRegistry, seed), () -> generatorSettingsRegistry.getOrThrow(generatorType.getGeneratorSettings()), generatorConfig, seed))));
		}
	}
}
