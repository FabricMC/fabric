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

package net.fabricmc.fabric.impl.datafixer;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;

public class FabricDataFixerImpl implements DataFixerHelper {
	private FabricDataFixerImpl() {
		this.fabricSchema = createSchema();
	}

	public static final Logger LOGGER = LogManager.getLogger(DataFixerHelper.class);
	public static FabricDataFixerImpl INSTANCE = new FabricDataFixerImpl();

	public final Schema fabricSchema;
	private final Map<String, DataFixerEntry> modDataFixers = new IdentityHashMap<>();
	private boolean locked;

	@Override
	public DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer) {
		Preconditions.checkNotNull(modid, "modid cannot be null");
		Preconditions.checkArgument(runtimeDataVersion >= 0, "dataVersion cannot be lower than 0");

		if (isLocked()) {
			throw new UnsupportedOperationException("Failed to register DataFixer for " + modid + ", registration is locked.");
		}

		LOGGER.info("Registered DataFixer for " + modid);
		modDataFixers.put(modid, new DataFixerEntry(datafixer, runtimeDataVersion));

		return datafixer;
	}

	@Override
	public DataFixer getDataFixer(String modid) {
		DataFixerEntry entry = modDataFixers.get(modid);

		if (entry != null) {
			return entry.dataFixer;
		}

		throw new IllegalArgumentException("No DataFixer is registered to " + modid);
	}

	@Override
	public int getModDataVersion(CompoundTag compoundTag, String modid) {
		return compoundTag.getInt(modid + "_DataVersion");
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	public CompoundTag updateWithAllFixers(DataFixTypes dataFixTypes, CompoundTag compoundTag) {
		CompoundTag currentTag = compoundTag;

		for (Map.Entry<String, DataFixerEntry> entry : modDataFixers.entrySet()) {
			String currentModid = entry.getKey();
			int modidCurrentDynamicVersion = getModDataVersion(compoundTag, currentModid);
			DataFixerEntry dataFixerEntry = entry.getValue();

			currentTag = (CompoundTag) dataFixerEntry.dataFixer.update(dataFixTypes.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, currentTag), modidCurrentDynamicVersion, dataFixerEntry.runtimeDataVersion).getValue();
		}

		return currentTag;
	}

	public void addFixerVersions(CompoundTag compoundTag) {
		for (Map.Entry<String, DataFixerEntry> entry : modDataFixers.entrySet()) {
			compoundTag.putInt(entry.getKey() + "_DataVersion", entry.getValue().runtimeDataVersion);
		}
	}

	public void lock() {
		if (!locked) {
			LOGGER.info("Locked DataFixer registration");
		}

		this.locked = true;
	}

	private Schema createSchema() {
		LOGGER.debug("Creating Fabric Schema Type");
		return new Schema(0, VanillaDataFixers.VANILLA_DATAFIXER.apply(-1, null));
	}

	static final class DataFixerEntry {
		private final DataFixer dataFixer;
		private final int runtimeDataVersion;

		DataFixerEntry(DataFixer fix, int runtimeDataVersion) {
			this.dataFixer = fix;
			this.runtimeDataVersion = runtimeDataVersion;
		}
	}

	/**
	 * Represents Minecraft's Built in DataFixer.
	 */
	public static final class VanillaDataFixers {
		private static final int LATEST_VANILLA_SCHEMA_VERSION = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());

		private static Schema getVanillaDataFixer(Integer integer, Schema schema) {
			LOGGER.info("[Fabric DataFixer] Started with a Vanilla Schema version of " + LATEST_VANILLA_SCHEMA_VERSION);
			return Schemas.getFixer().getSchema(LATEST_VANILLA_SCHEMA_VERSION);
		}

		public static final BiFunction<Integer, Schema, Schema> VANILLA_DATAFIXER = (version, parent) -> Schemas.getFixer().getSchema(LATEST_VANILLA_SCHEMA_VERSION);
	}
}
