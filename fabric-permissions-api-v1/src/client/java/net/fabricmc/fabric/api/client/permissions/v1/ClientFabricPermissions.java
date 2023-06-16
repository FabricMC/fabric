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

package net.fabricmc.fabric.api.client.permissions.v1;

import net.fabricmc.fabric.impl.permissions.client.ClientModListHandler;

import java.util.Collections;
import java.util.List;

public final class ClientFabricPermissions {

	/**
	 * A list of illegal features as imposed by the server the client
	 * is currently connected to.
	 *
	 * @return a list of disallowed features
	 */
	public static List<String> getIllegalFeatures() {
		return Collections.unmodifiableList(ClientModListHandler.illegalFeatures);
	}

	/**
	 * Checks whether a feature is illegal as imposed by the server
	 * the client is currently connected to.
	 *
	 * @param feature the feature to check
	 * @return {@code true} if this feature is illegal, {@code false} otherwise
	 */
	public static boolean isFeatureDisallowed(String feature) {
		return ClientModListHandler.illegalFeatures.contains(feature);
	}
}
