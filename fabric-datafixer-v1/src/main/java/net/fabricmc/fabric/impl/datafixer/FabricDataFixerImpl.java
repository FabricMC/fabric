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

import com.google.common.base.Preconditions;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;


/**
 * 
 * Assume I have two mods that both register DataFixers.
 * And mod A's item is being fixed but is inside mod B's BE
 * So when Mod A fixes it's own item and it doesn't know it's item exists within mod B's BE (Because mod B's type reference is non-existent to mod A)
 * So either I need to register every single entity/blockEntity underneath the root V0 Schema or find another way.
 *
 * method_5346 (registerWithItemRefs) within Schema99 would add the typeReference to the base schema. 
 *
 */
public final class FabricDataFixerImpl implements DataFixerHelper {

	private static final Logger LOGGER = LogManager.getLogger("Fabric-DataFixer");
	public static final Schema FABRIC_SCHEMA;
	public static final FabricDataFixerImpl INSTANCE = new FabricDataFixerImpl();

	static {
		FABRIC_SCHEMA = createSchema();
	}

	private final Map<String, DataFixerEntry> modFixers = new HashMap<>();
	private boolean locked;

	private FabricDataFixerImpl() {}

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
	public Optional<DataFixer> getDataFixer(String modid) {
		return Optional.ofNullable(modFixers.get(modid).modFixer);
	}
	
	/**
	 * Registers a DataFixer to be used to automatically fix types when CompoundTags are loaded.
	 */
	@Override
	public DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer) {
		Preconditions.checkNotNull(modid, "modid cannot be null");
		Preconditions.checkArgument(runtimeDataVersion >= 0, "dataVersion cannot be lower than 0");

		modFixers.put(modid, new DataFixerEntry(datafixer, runtimeDataVersion));

		return datafixer;
	}

	public CompoundTag updateWithAllFixers(DataFixTypes dataFixTypes, CompoundTag compoundTag) {

		CompoundTag currentTag = compoundTag;

		for (Entry<String, DataFixerEntry> entry : modFixers.entrySet()) {
			String currentModid = entry.getKey();
			int modidCurrentDynamicVersion = getModDataVersion(compoundTag, currentModid);
			DataFixerEntry dataFixerEntry = entry.getValue();

			currentTag = (CompoundTag) dataFixerEntry.modFixer.update(dataFixTypes.getTypeReference(), new Dynamic<Tag>(NbtOps.INSTANCE, currentTag), modidCurrentDynamicVersion, dataFixerEntry.runtimeDataVersion).getValue();
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
		this.locked = true;
	}

	private static Schema createSchema() {
		List<DataFixerEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("fabric:datafixer", DataFixerEntrypoint.class);
		Schema schema = new Schema(0, MCDFU.MC_TYPE_REFS.apply(-1, null)) {

			@Override
			public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
				super.registerTypes(schema, entityTypes, blockEntityTypes);
				for (DataFixerEntrypoint entrypoint : entrypoints) {
					entrypoint.registerTypes(schema, entityTypes, blockEntityTypes);
				}
			}

			@Override
			public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
				Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

				for (DataFixerEntrypoint entrypoint : entrypoints) {
					entrypoint.registerBlockEntities(schema, map);
				}
				return map;
			}

			@Override
			public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
				Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

				for (DataFixerEntrypoint entrypoint : entrypoints) {
					entrypoint.registerEntities(schema, map);
				}
				return map;
			}
		};
		return schema;
	}

	final class DataFixerEntry {
		private final DataFixer modFixer;
		private final int runtimeDataVersion;

		DataFixerEntry(DataFixer fix, int runtimeDataVersion) {
			this.modFixer = fix;
			this.runtimeDataVersion = runtimeDataVersion;
		}
	}
}
