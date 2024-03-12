/*
 * Copyright (c) 2016-2022 FabricMC
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
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

package net.fabricmc.fabric.impl.datafixer.v1;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;

import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public abstract class FabricDataFixesInternals {
	// From QSL.
	private static final Logger LOGGER = LogUtils.getLogger();
	protected static final String DATA_VERSIONS_KEY = "_FabricDataVersions";
	public static final String METADATA_VERSION_KEY = "fabric-data-fixer-api-v1:version";
	public static final String METADATA_KEY_KEY = "fabric-data-fixer-api-v1:key";

	public record DataFixerEntry(DataFixer dataFixer, int currentVersion, @Nullable String key) {
	}

	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getDataVersionFromMetadata(ModContainer mod) {
		CustomValue version = mod.getMetadata().getCustomValue(METADATA_VERSION_KEY);

		if (version == null || version.getType() != CustomValue.CvType.NUMBER) {
			throw new RuntimeException("Data version is not set in the fabric.mod.json file; set it or pass explicitly");
		}

		return version.getAsNumber().intValue();
	}

	@Nullable
	public static String getKeyFromMetadata(ModContainer mod) {
		CustomValue key = mod.getMetadata().getCustomValue(METADATA_KEY_KEY);

		if (key == null) {
			return null;
		}

		if (key.getType() != CustomValue.CvType.NUMBER) {
			throw new RuntimeException("Key is not set in the fabric.mod.json file; set it or pass explicitly");
		}

		return key.getAsString();
	}

	@Contract(pure = true)
	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getModDataVersion(NbtCompound nbt, String modId, @Nullable String key) {
		NbtCompound dataVersions = nbt.getCompound(DATA_VERSIONS_KEY);
		return dataVersions.getInt(modId);
	}

	private static FabricDataFixesInternals instance;

	public static FabricDataFixesInternals get() {
		if (instance == null) {
			Schema latestVanillaSchema;

			try {
				latestVanillaSchema = Schemas.getFixer()
						.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getSaveVersion().getId()));
			} catch (Throwable e) {
				latestVanillaSchema = null;
			}

			if (latestVanillaSchema == null) {
				LOGGER.warn("[Fabric DFU API] Failed to initialize! Either someone stopped DFU from initializing,");
				LOGGER.warn("[Fabric DFU API]  or this Minecraft build is hosed.");
				LOGGER.warn("[Fabric DFU API] Using no-op implementation.");
				instance = new NoOpFabricDataFixesInternals();
			} else {
				instance = new FabricDataFixesInternalsImpl(latestVanillaSchema);
			}
		}

		return instance;
	}

	public abstract void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @Nullable String key, DataFixer dataFixer);

	public abstract @Nullable List<DataFixerEntry> getFixerEntries(String modId);

	@Contract(value = "-> new", pure = true)
	public abstract Schema createBaseSchema();

	public abstract Dynamic<NbtElement> updateWithAllFixers(DataFixTypes dataFixTypes, Dynamic<NbtElement> element);

	public abstract NbtCompound addModDataVersions(NbtCompound nbt);

	public abstract void registerBlockEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema);

	public abstract void registerEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema);

	public abstract void freeze();

	@Contract(pure = true)
	public abstract boolean isFrozen();
}
