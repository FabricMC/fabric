/*
 * Copyright 2022 QuiltMC
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

package net.fabricmc.fabric.impl.datafixer.v1;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.*;
import org.slf4j.Logger;

import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;

@ApiStatus.Internal
public abstract class FabricDataFixesInternals {
	private static final Logger LOGGER = LogUtils.getLogger();

	public record DataFixerEntry(DataFixer dataFixer, int currentVersion) {}

	@Contract(pure = true)
	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getModDataVersion(@NotNull NbtCompound compound, @NotNull String modId) {
		return compound.getInt(modId + "_DataVersion");
	}

	private static FabricDataFixesInternals instance;

	public static @NotNull FabricDataFixesInternals get() {
		if (instance == null) {
			Schema latestVanillaSchema;
			try {
				latestVanillaSchema = Schemas.getFixer()
						.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getSaveVersion().getId()));
			} catch (Exception e) {
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

	public abstract void registerFixer(@NotNull String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			@NotNull DataFixer dataFixer);

	public abstract @Nullable DataFixerEntry getFixerEntry(@NotNull String modId);

	@Contract(value = "-> new", pure = true)
	public abstract @NotNull Schema createBaseSchema();

	public abstract @NotNull NbtCompound updateWithAllFixers(@NotNull DataFixTypes dataFixTypes, @NotNull NbtCompound compound);

	public abstract @NotNull NbtCompound addModDataVersions(@NotNull NbtCompound compound);

	public abstract void freeze();

	@Contract(pure = true)
	public abstract boolean isFrozen();
}
