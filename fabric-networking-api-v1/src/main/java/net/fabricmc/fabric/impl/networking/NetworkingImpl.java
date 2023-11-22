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

package net.fabricmc.fabric.impl.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.payload.TypedPayload;
import net.fabricmc.fabric.impl.networking.payload.UntypedPayload;
import net.fabricmc.loader.api.FabricLoader;

public final class NetworkingImpl {
	public static final String MOD_ID = "fabric-networking-api-v1";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Force {@link TypedPayload} to be serialized into {@link UntypedPayload}, mimicking remote connection.
	 *
	 * <p>Defaults to {@code true} in dev env and {@code false} in production.
	 */
	public static final boolean FORCE_PACKET_SERIALIZATION = Boolean.parseBoolean(System.getProperty(
			"fabric-api.networking.force-packet-serialization",
			Boolean.toString(FabricLoader.getInstance().isDevelopmentEnvironment())));

	/**
	 * Id of packet used to register supported channels.
	 */
	public static final Identifier REGISTER_CHANNEL = new Identifier("minecraft", "register");

	/**
	 * Id of packet used to unregister supported channels.
	 */
	public static final Identifier UNREGISTER_CHANNEL = new Identifier("minecraft", "unregister");

	public static boolean isReservedCommonChannel(Identifier channelName) {
		return channelName.equals(REGISTER_CHANNEL) || channelName.equals(UNREGISTER_CHANNEL);
	}

	static {
		if (FORCE_PACKET_SERIALIZATION) {
			LOGGER.info("Force Packet Serialization is enabled to mimic remote connection on single player, this is the default behaviour on dev env. Add -Dfabric-api.networking.force-packet-serialization=false JVM arg to disable it.");
		}
	}
}
