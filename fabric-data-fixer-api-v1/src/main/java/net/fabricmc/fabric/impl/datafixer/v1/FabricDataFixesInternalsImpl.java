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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public final class FabricDataFixesInternalsImpl extends FabricDataFixesInternals {
	// From QSL.
	private Schema latestSchema;

	private Map<String, List<DataFixerEntry>> modDataFixers;
	private boolean frozen;

	public FabricDataFixesInternalsImpl(Schema latestVanillaSchema) {
		this.latestSchema = latestVanillaSchema;
		this.modDataFixers = new Object2ReferenceOpenHashMap<>();
		this.frozen = false;
	}

	@Override
	public void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			@Nullable String key, DataFixerUpper dataFixer) {
		Int2ObjectSortedMap<Schema> schemas = ((DataFixerUpperExtension) dataFixer).fabric_getSchemas();
		Schema lastSchema = schemas.getOrDefault(schemas.lastIntKey(), null);

		if (lastSchema != null) {
			this.latestSchema = lastSchema;
		}

		this.modDataFixers.computeIfAbsent(modId, modIdx -> new ObjectArrayList<>())
				.add(new DataFixerEntry(dataFixer, currentVersion, key));
	}

	@Override
	public @Nullable List<DataFixerEntry> getFixerEntries(String modId) {
		return modDataFixers.get(modId);
	}

	@Override
	public Schema getBaseSchema() {
		return new Schema(0, this.latestSchema);
	}

	@Override
	public Dynamic<NbtElement> updateWithAllFixers(DataFixTypes dataFixTypes, Dynamic<NbtElement> current) {
		NbtCompound compound = (NbtCompound) current.getValue();

		Map<DataFixerEntry, Integer> fixers = new HashMap<>();

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
