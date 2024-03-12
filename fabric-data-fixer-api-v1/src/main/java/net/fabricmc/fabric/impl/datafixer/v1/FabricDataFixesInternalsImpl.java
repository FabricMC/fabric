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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public final class FabricDataFixesInternalsImpl extends FabricDataFixesInternals {
	// From QSL.
	private static final String ENTRYPOINT_KEY = "fabric-data-fixer";

	private final Schema latestVanillaSchema;

	private Map<String, List<DataFixerEntry>> modDataFixers;
	private boolean frozen;

	public FabricDataFixesInternalsImpl(Schema latestVanillaSchema) {
		this.latestVanillaSchema = latestVanillaSchema;

		this.modDataFixers = new Object2ReferenceOpenHashMap<>();
		this.frozen = false;
	}

	@Override
	public void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			@Nullable String key, DataFixer dataFixer) {
		if (this.modDataFixers.containsKey(modId)) {
			throw new IllegalArgumentException("Mod '" + modId + "' has already registered a data fixer");
		}

		this.modDataFixers.computeIfAbsent(modId, modIdx -> new ArrayList<>())
				.add(new DataFixerEntry(dataFixer, currentVersion, key));
	}

	@Override
	public @Nullable List<DataFixerEntry> getFixerEntries(String modId) {
		return modDataFixers.get(modId);
	}

	@Override
	public Schema createBaseSchema() {
		return new Schema(0, this.latestVanillaSchema);
	}

	@Override
	public Dynamic<NbtElement> updateWithAllFixers(DataFixTypes dataFixTypes, Dynamic<NbtElement> current) {
		NbtCompound compound = (NbtCompound) current.getValue();

		for (Map.Entry<String, List<DataFixerEntry>> entry : this.modDataFixers.entrySet()) {
			List<DataFixerEntry> dataFixerEntries = entry.getValue();

			for (DataFixerEntry dataFixerEntry : dataFixerEntries) {
				int modDataVersion = FabricDataFixesInternals.getModDataVersion(compound, entry.getKey(), dataFixerEntry.key());

				current = dataFixerEntry.dataFixer()
						.update(dataFixTypes.typeReference,
								current,
								modDataVersion, dataFixerEntry.currentVersion());
			}
		}

		return current;
	}

	@Override
	public NbtCompound addModDataVersions(NbtCompound nbt) {
		NbtCompound dataVersions = nbt.getCompound(DATA_VERSIONS_KEY);

		for (Map.Entry<String, List<DataFixerEntry>> entries : this.modDataFixers.entrySet()) {
			for (DataFixerEntry entry : entries.getValue()) {
				String finalEntryKey = entries.getKey();
				String entryKey = entry.key();

				if (entryKey != null) {
					finalEntryKey += ('_' + entryKey);
				}

				dataVersions.putInt(finalEntryKey, entry.currentVersion());
			}
		}

		nbt.put(DATA_VERSIONS_KEY, dataVersions);
		return nbt;
	}

	private static List<DataFixerEntrypoint> getEntrypoints() {
		List<EntrypointContainer<DataFixerEntrypoint>> dataFixerEntrypoints = FabricLoader.getInstance()
			.getEntrypointContainers(ENTRYPOINT_KEY, DataFixerEntrypoint.class);
		return dataFixerEntrypoints.stream().map(EntrypointContainer::getEntrypoint).toList();
	}

	@Override
	public void registerBlockEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema) {
		List<DataFixerEntrypoint> entrypoints = getEntrypoints();

		for (DataFixerEntrypoint entrypoint : entrypoints) {
			entrypoint.onRegisterBlockEntities(registry, schema);
		}
	}

	@Override
	public void registerEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema) {
		List<DataFixerEntrypoint> entrypoints = getEntrypoints();

		for (DataFixerEntrypiont entrypoint : entrypoints) {
			entrypoint.onRegisterEntities(registry, schema);
		}
	}

	@Override
	public void freeze() {
		if (!this.frozen) {
			modDataFixers = Collections.unmodifiableMap(this.modDataFixers);
		}

		this.frozen = true;
	}

	@Override
	public boolean isFrozen() {
		return this.frozen;
	}
}
