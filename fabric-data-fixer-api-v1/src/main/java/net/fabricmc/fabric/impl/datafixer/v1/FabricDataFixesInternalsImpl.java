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
import java.util.Map;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

@ApiStatus.Internal
public final class FabricDataFixesInternalsImpl extends FabricDataFixesInternals {
	private final Schema latestVanillaSchema;

	private Map<String, DataFixerEntry> modDataFixers;
	private boolean frozen;

	public FabricDataFixesInternalsImpl(Schema latestVanillaSchema) {
		this.latestVanillaSchema = latestVanillaSchema;

		this.modDataFixers = new Object2ReferenceOpenHashMap<>();
		this.frozen = false;
	}

	@Override
	public void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			DataFixer dataFixer) {
		if (this.modDataFixers.containsKey(modId)) {
			throw new IllegalArgumentException("Mod '" + modId + "' already has a registered data fixer");
		}

		this.modDataFixers.put(modId, new DataFixerEntry(dataFixer, currentVersion));
	}

	@Override
	public @Nullable DataFixerEntry getFixerEntry(String modId) {
		return modDataFixers.get(modId);
	}

	@Override
	public Schema createBaseSchema() {
		return new Schema(0, this.latestVanillaSchema);
	}

	@Override
	public NbtCompound updateWithAllFixers(DataFixTypes dataFixTypes, NbtCompound compound) {
		Dynamic<NbtElement> current = new Dynamic<>(NbtOps.INSTANCE, compound);

		for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
			int modDataVersion = FabricDataFixesInternals.getModDataVersion(compound, entry.getKey());
			DataFixerEntry dataFixerEntry = entry.getValue();

			current = dataFixerEntry.dataFixer()
					.update(dataFixTypes.getTypeReference(),
							current,
							modDataVersion, dataFixerEntry.currentVersion());
		}

		return (NbtCompound) current.getValue();
	}

	@Override
	public NbtCompound addModDataVersions(NbtCompound nbt) {
		NbtCompound dataVersions = nbt.getCompound(DATA_VERSIONS_KEY);

		for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
			dataVersions.putInt(entry.getKey(), entry.getValue().currentVersion());
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
