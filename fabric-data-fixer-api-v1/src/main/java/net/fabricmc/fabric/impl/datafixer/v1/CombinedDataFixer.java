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

import java.util.Map;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.Nullable;

public interface CombinedDataFixer {
	<T> Dynamic<T> update(DSL.TypeReference type, Dynamic<T> input, Map<FabricDataFixesInternals.DataFixerEntry, Integer> versionUpgrades);

	default Schema getSchema(String modId, int version) {
		return getSchema(modId, null, version);
	}

	Schema getSchema(String modId, @Nullable String key, int version);
}
