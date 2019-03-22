/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.config;

import net.fabricmc.fabric.api.config.ConfigBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Fabric's personal config manager. Stores mods' configs and provides hooks to interact with configs.
 */
public class ConfigManager {
	public static Map<String, ConfigBuilder> modConfigs = new HashMap<>();

	/**
	 * Get the config builder for a mod.
	 * @param modId the ID of the mod to get a builder from
	 * @return the ConfigBuilder for the given mod
	 */
	public static ConfigBuilder getConfig(String modId) {
		return modConfigs.get(modId);
	}

	/**
	 * Add a config builder for a mod. Throws if one is already present.
	 * @param modId the ID of the mod to add a builder for
	 * @param builder the ConfigBuilder for the mod
	 */
	public static void addConfig(String modId, ConfigBuilder builder) {
		if (hasBuilder(modId)) throw new UnsupportedOperationException("Cannot have more than one config builder per mod : " + modId);
		modConfigs.put(modId, builder);
	}

	/**
	 * Check if a mod already has a builder registered.
	 * @param modId the ID of the mod to check
	 * @return whether a builder exists.
	 */
	public static boolean hasBuilder(String modId) {
		return modConfigs.containsKey(modId);
	}
}
