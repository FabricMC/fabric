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

package net.fabricmc.fabric.mixin.dimension;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;

/*
 * This is a bug fix.
 * This fixes an issue where removing a mod or datapack that adds a dimension will cause every dimension but the overworld to also be disposed of.
 *
 * Testing:
 * A good way to test this is to create a world with the dimension test mod enabled and then without to simulate a dimension removal.
 *
 * Tracking issue: https://bugs.mojang.com/browse/MC-197860
 */
@Mixin(LevelStorage.class)
abstract class LevelStorageMixin {
	/*
	 * When the custom dimension is removed, DimensionGeneratorSettings.CODEC fails to fully parse the Dynamic<T>,
	 * so the DataResult<DimensionGeneratorSettings> only return the Either#Right part of the result,
	 * which only includes the overworld.
	 *
	 * This causes all dimensions to cease to exist.
	 * To fix this issue, we will intercept the dynamic being parsed, and remove any dimension entries that are NOT in the registry.
	 */

	/**
	 * @param dynamic the dynamic containing the properties
	 * @param dataFixer the data fixer
	 * @param version the data version of the properties being read
	 * @param info the callback info
	 * @param propertiesDynamic the dynamic containing the world gen settings
	 * @param <T> the type in the dynamic
	 */
	@Inject(method = "readGeneratorProperties", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/DataFixer;update(Lcom/mojang/datafixers/DSL$TypeReference;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static <T> void fixDimensionsOnLoad(Dynamic<T> dynamic, DataFixer dataFixer, int version, CallbackInfoReturnable<Pair<GeneratorOptions, Lifecycle>> info, Dynamic<T> propertiesDynamic) {
		CompoundTag properties;

		// Fix the dynamic in place if the value is nbt
		if (dynamic.getValue() instanceof CompoundTag) {
			properties = (CompoundTag) dynamic.getValue();
		} else {
			// Not supported atm
			throw new IllegalStateException("Dynamic is not backed by a Compound Tag! This is not supported at the moment.");
		}

		// Get the dimension registry
		DataResult<Pair<Registry<DimensionType>, T>> result = RegistryLookupCodec.of(Registry.DIMENSION_TYPE_KEY).codec().decode(propertiesDynamic);
		Registry<DimensionType> dimensionRegistry = result.result().orElseThrow(() -> {
			return new RuntimeException("Failed to get the dimension registry");
		}).getFirst();

		CompoundTag dimensions = properties.getCompound("WorldGenSettings").getCompound("dimensions");

		for (String key : dimensions.getKeys()) {
			Identifier identifier;

			try {
				identifier = new Identifier(key);
			} catch (InvalidIdentifierException e) {
				throw new RuntimeException("Failed to fix generator properties because of invalid dimension identifier", e);
			}

			// Dimension is not in registry, remove it from the compound tag so DFU does not freak out
			if (!dimensionRegistry.containsId(identifier)) {
				dimensions.remove(key);
			}
		}
	}
}
