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

package net.fabricmc.fabric.api.datafixer.v1;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class FabricDataFixerUpper extends DataFixerUpper {
	private final Int2ObjectSortedMap<Schema> schemas;
	private final List<DataFix> globalList;
	private final IntSortedSet fixerVersions;
	private final Long2ObjectMap<List<TypeRewriteRule>> rules = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());

	public FabricDataFixerUpper(final Int2ObjectSortedMap<Schema> schemas, final List<DataFix> globalList, final IntSortedSet fixerVersions) {
		super(schemas, globalList, fixerVersions);
		this.schemas = schemas;
		this.globalList = globalList;
		this.fixerVersions = fixerVersions;
	}

	@Override
	public Type<?> getType(final DSL.TypeReference type, final int version) {
		return super.getType(type, version);
	}

	public static int lowestSchemaSameVersion(final Int2ObjectSortedMap<Schema> schemas, final int versionKey) {
		return getLowestSchemaSameVersion(schemas, versionKey);
	}

	private int getLowestFixSameVersion(final int versionKey) {
		if (versionKey < fixerVersions.firstInt()) {
			// can have a version before everything else
			return fixerVersions.firstInt() - 1;
		}

		return fixerVersions.subSet(0, versionKey + 1).lastInt();
	}

	public void addRules(List<TypeRewriteRule> list, final int version, final int newVersion) {
		if (version >= newVersion) {
			return;
		}

		final long key = (long) version << 32 | newVersion;
		list.addAll(this.rules.computeIfAbsent(key, k -> {
			final int expandedVersion = getLowestFixSameVersion(DataFixUtils.makeKey(version));

			final List<TypeRewriteRule> rules = Lists.newArrayList();

			for (final DataFix fix : globalList) {
				final int expandedFixVersion = fix.getVersionKey();
				final int fixVersion = DataFixUtils.getVersion(expandedFixVersion);

				if (expandedFixVersion > expandedVersion && fixVersion <= newVersion) {
					final TypeRewriteRule fixRule = fix.getRule();

					if (fixRule == TypeRewriteRule.nop()) {
						continue;
					}

					rules.add(fixRule);
				}
			}

			return rules;
		}));
	}
}
