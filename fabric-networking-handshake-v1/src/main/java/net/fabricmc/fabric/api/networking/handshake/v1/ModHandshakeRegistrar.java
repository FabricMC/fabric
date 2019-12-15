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

package net.fabricmc.fabric.api.networking.handshake.v1;

import com.google.common.collect.Multimap;

/**
 * The entrypoint type for registering custom mod handshake handlers.
 *
 * <p>The entry point's name is "fabric-networking-handshake".
 *
 * @see ModVersionReporter
 */
@FunctionalInterface
public interface ModHandshakeRegistrar {
	/**
	 * Register mod version reporters to a registry.
	 *
	 * @param registry the registry
	 */
	void registerTo(Multimap<String, ModVersionReporter> registry);
}
