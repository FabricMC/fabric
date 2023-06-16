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

package net.fabricmc.fabric.api.permissions.v1;

import net.fabricmc.fabric.impl.permissions.FabricPermissionsImpl;
import net.fabricmc.fabric.impl.permissions.ModListHandler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FabricPermissions {

	/**
	 * Returns whether Fabric Permissions are enabled on this server.
	 *
	 * @return {@code true} if Fabric Permissions are enabled, {@code false} otherwise
	 */
	public static boolean isEnabled() {
		return FabricPermissionsImpl.enabled;
	}

	/**
	 * The current response policy to illegal mods.
	 * @return the current response policy
	 */
	public static IllegalModResponsePolicy getResponsePolicy() {
		return FabricPermissionsImpl.illegalModResponsePolicy;
	}

	/**
	 * The list of illegal mods as specified by the permissions file.
	 *
	 * @return the list of illegal mods
	 */
	public static List<String> getIllegalMods() {
		return Collections.unmodifiableList(FabricPermissionsImpl.illegalMods);
	}

	/**
	 * The list of illegal features as specified by the permissions file.
	 *
	 * @return the list of illegal features
	 */
	public static List<String> getIllegalFeatures() {
		return Collections.unmodifiableList(FabricPermissionsImpl.illegalFeatures);
	}

	/**
	 * A list of mods the specified player is currently using.
	 *
	 * @param player the player
	 * @return a list of mods or an empty list
	 */
	public static List<String> getModsForPlayer(UUID player) {
		List<String> modIds = ModListHandler.modsForPlayer.get(player);
		if (modIds == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(modIds);
	}
}
