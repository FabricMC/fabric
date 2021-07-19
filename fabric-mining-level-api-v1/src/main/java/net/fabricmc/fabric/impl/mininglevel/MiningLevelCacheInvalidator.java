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

package net.fabricmc.fabric.impl.mininglevel;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

final class MiningLevelCacheInvalidator implements SimpleSynchronousResourceReloadListener {
	private static final Identifier ID = new Identifier("fabric-mining-level-api-v1", "cache_invalidator");
	private static final Set<Identifier> DEPENDENCIES = Collections.singleton(ResourceReloadListenerKeys.TAGS);

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return DEPENDENCIES;
	}

	@Override
	public void reload(ResourceManager manager) {
		MiningLevelManagerImpl.clearCache();
	}
}
