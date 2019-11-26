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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.datafixers.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.SharedConstants;

import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricDataFixerImpl implements DataFixerHelper {
	public static final Logger LOGGER = LogManager.getLogger("Fabric-DataFixer", new AbstractMessageFactory() {
		@Override
		public Message newMessage(String message, Object... params) {
			return new SimpleMessage("[Fabric-DataFixer] " + message);
		}
	});
	public static final FabricDataFixerImpl INSTANCE = new FabricDataFixerImpl();
	public final Schema fabricSchema;
	private final Map<String, DataFixerEntry> modFixers = new HashMap<>();
	private boolean locked;

	private FabricDataFixerImpl() {
		fabricSchema = createSchema();
	}

	public void addFixerVersions(CompoundTag compoundTag) {
		for (Entry<String, DataFixerEntry> entry : modFixers.entrySet()) {
			compoundTag.putInt(entry.getKey() + "_DataVersion", entry.getValue().runtimeDataVersion);
		}
	}

	@Override
	public int getModDataVersion(CompoundTag compoundTag, String modid) {
		return compoundTag.getInt(modid + "_DataVersion");
	}

	@Override
	public Type<?> getChoiceType(DataFixer dataFixer, int schemaVersion, DSL.TypeReference typeReference, String identifier) {
		Preconditions.checkNotNull(dataFixer, "DataFixer cannot be null");
		Preconditions.checkNotNull(typeReference, "TypeReference cannot be null");
		Preconditions.checkNotNull(identifier, "Identifier cannot be null");

		Schema schema = dataFixer.getSchema(DataFixUtils.makeKey(schemaVersion));

		if (schema == null) {
			throw new IllegalArgumentException("DataFixer does not contain a Schema with a version of " + schemaVersion);
		}

		return schema.getChoiceType(typeReference, identifier);
	}

	@Override
	public DataFixer getDataFixer(String modid) {
		DataFixerEntry entry = modFixers.get(modid);

		if (entry != null) {
			return entry.dataFixer;
		}

		throw new IllegalArgumentException("No DataFixer is registered to " + modid);
	}

	@Override
	public DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer) {
		Preconditions.checkNotNull(modid, "modid cannot be null");
		Preconditions.checkArgument(runtimeDataVersion >= 0, "dataVersion cannot be lower than 0");

		if (isLocked()) {
			throw new UnsupportedOperationException("Failed to register DataFixer for " + modid + ", registration is locked.");
		}

		LOGGER.info("Registered DataFixer for " + modid);
		modFixers.put(modid, new DataFixerEntry(datafixer, runtimeDataVersion));

		return datafixer;
	}

	public CompoundTag updateWithAllFixers(DataFixTypes dataFixTypes, CompoundTag compoundTag) {
		LOGGER.debug("DataFixing a CompoundTag");
		CompoundTag currentTag = compoundTag;

		for (Entry<String, DataFixerEntry> entry : modFixers.entrySet()) {
			String currentModid = entry.getKey();
			LOGGER.debug("Fixing Data for " + currentModid);
			int modidCurrentDynamicVersion = getModDataVersion(compoundTag, currentModid);
			DataFixerEntry dataFixerEntry = entry.getValue();

			currentTag = (CompoundTag) dataFixerEntry.dataFixer.update(dataFixTypes.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, currentTag), modidCurrentDynamicVersion, dataFixerEntry.runtimeDataVersion).getValue();
		}

		return currentTag;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @deprecated for implementation only.
	 */
	public void lock() {
		if (!locked) {
			LOGGER.info("Locked DataFixer registration");
		}

		this.locked = true;
	}

	private static Schema createSchema() {
		LOGGER.debug("Creating Fabric Schema Type");
		List<DataFixerEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("fabric:datafixer", DataFixerEntrypoint.class);
		LOGGER.debug("Found " + entrypoints.size() + " entrypoints.");
		Schema schema = new Schema(0, VanillaDataFixers.VANILLA_DATAFIXER.apply(-1, null)) {
			@Override
			public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
				super.registerTypes(schema, entityTypes, blockEntityTypes);

				for (DataFixerEntrypoint entrypoint : entrypoints) {
					LOGGER.debug("Registering types from " + entrypoint.getClass().getCanonicalName());
					entrypoint.registerTypes(schema, entityTypes, blockEntityTypes);
				}
			}

			@Override
			public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
				Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

				for (DataFixerEntrypoint entrypoint : entrypoints) {
					LOGGER.debug("Registering BlockEntities from " + entrypoint.getClass().getCanonicalName());
					entrypoint.registerBlockEntities(schema, map);
				}

				return map;
			}

			@Override
			public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
				Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

				for (DataFixerEntrypoint entrypoint : entrypoints) {
					LOGGER.debug("Registering Entities from " + entrypoint.getClass().getCanonicalName());
					entrypoint.registerEntities(schema, map);
				}

				return map;
			}
		};
		return schema;
	}

	/**
	 * An Entry which stores DataFixers for use by implementation.
	 */
	final class DataFixerEntry {
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

		static {
			LOGGER.info("Started with Vanilla-DataFixer version: " + LATEST_VANILLA_SCHEMA_VERSION);
		}

		public static final BiFunction<Integer, Schema, Schema> VANILLA_DATAFIXER = (version, parent) -> Schemas.getFixer().getSchema(LATEST_VANILLA_SCHEMA_VERSION);
	}
}
