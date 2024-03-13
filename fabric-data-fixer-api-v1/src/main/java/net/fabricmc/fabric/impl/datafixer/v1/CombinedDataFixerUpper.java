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

package net.fabricmc.fabric.impl.datafixer.v1;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerUpper;

public class CombinedDataFixerUpper implements CombinedDataFixer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CombinedDataFixerUpper.class);

	// copy of DataFixerUpper's optimization rule
	protected static final PointFreeRule OPTIMIZATION_RULE = DataFixUtils.make(() -> PointFreeRule.everywhere(
			// Top-down: these rules produce new compositions that also need to be rewritten
			PointFreeRule.seq(
					// Applying CataFuseDifferent before CataFuseSame would prevent some merges from happening, but not the other way around
					PointFreeRule.CataFuseSame.INSTANCE,
					PointFreeRule.CataFuseDifferent.INSTANCE,
					// Apply all of these together exhaustively because each change can allow another rule to apply
					PointFreeRule.CompRewrite.together(
							// Merge functions applying to identical optics, must run before merging nested applied functions
							PointFreeRule.LensComp.INSTANCE,
							PointFreeRule.SortProj.INSTANCE,
							PointFreeRule.SortInj.INSTANCE
					)
			),
			// Bottom-up: ensure we nest the full tree in a single pass
			PointFreeRule.AppNest.INSTANCE
	));

	// map of mod id -> datafixer
	private final Map<String, List<FabricDataFixesInternals.DataFixerEntry>> modDataFixers;

	protected CombinedDataFixerUpper(final Map<String, List<FabricDataFixesInternals.DataFixerEntry>> modDataFixers) {
		this.modDataFixers = modDataFixers;
	}

	@Override
	public <T> Dynamic<T> update(final DSL.TypeReference type, final Dynamic<T> input, final Map<FabricDataFixesInternals.DataFixerEntry, Integer> versionUpgrades) {
		if (this.modDataFixers().isEmpty()) {
			return input;
		}

		List<TypeRewriteRule> rules = Lists.newArrayList();
		Type<?> dataType = null;
		Type<?> newType = null;

		for (Map.Entry<FabricDataFixesInternals.DataFixerEntry, Integer> upgrade : versionUpgrades.entrySet()) {
			FabricDataFixesInternals.DataFixerEntry entry = upgrade.getKey();
			FabricDataFixerUpper dataFixer = entry.dataFixer();

			int version = upgrade.getValue();
			int newVersion = entry.currentVersion();

			if (version < newVersion) {
				Type<?> dataType1 = dataFixer.getType(type, version);
				Type<?> newType1 = dataFixer.getType(type, newVersion);

				if (dataType == null) {
					dataType = dataType1;
				} else if (dataType != dataType1) {
					continue;
				}

				if (newType == null) {
					newType = newType1;
				} else if (newType != newType1) {
					continue;
				}

				dataFixer.addRules(rules, version, newVersion);
			}
		}

		if (dataType == null || newType == null) {
			return input;
		}

		final DataResult<T> read = dataType.readAndWrite(input.getOps(), newType, TypeRewriteRule.seq(rules), OPTIMIZATION_RULE, input.getValue());
		final T result = read.resultOrPartial(LOGGER::error).orElse(input.getValue());
		return new Dynamic<>(input.getOps(), result);
	}

	@Override
	public Schema getSchema(String modId, int version) {
		return this.modDataFixers().get(modId).get(0).dataFixer().getSchema(version);
	}

	@Override
	public Schema getSchema(String modId, @Nullable String key, int version) {
		String finalKey = modId;

		if (key != null) {
			finalKey += ('_' + key);
		}

		List<FabricDataFixesInternals.DataFixerEntry> entries = this.modDataFixers().get(finalKey);

		for (FabricDataFixesInternals.DataFixerEntry entry : entries) {
			if (Objects.equals(key, entry.key())) {
				return entry.dataFixer().getSchema(version);
			}
		}

		return null;
	}

	protected Map<String, List<FabricDataFixesInternals.DataFixerEntry>> modDataFixers() {
		return this.modDataFixers;
	}
}
