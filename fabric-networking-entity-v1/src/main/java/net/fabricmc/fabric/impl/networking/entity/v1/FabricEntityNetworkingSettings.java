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

package net.fabricmc.fabric.impl.networking.entity.v1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

public final class FabricEntityNetworkingSettings {
	public static final Identifier SPAWN_ENTITY_CHANNEL = new Identifier("fabric-networking-entity", "spawn_entity");
	public static final Logger LOGGER = LogManager.getLogger("Fabric Entity Networking");

	// System config values
	public static final boolean WARN_INVALID_FABRIC_PACKET = !Boolean.getBoolean("fabric.networking.entity.muteFabricSpawnPacket");
	public static final boolean WARN_INVALID_VANILLA_PACKET = !Boolean.getBoolean("fabric.networking.entity.muteVanillaSpawnPacket");

	private FabricEntityNetworkingSettings() {
	}
}
