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

package net.fabricmc.fabric.api.server.consent.v1.client;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.server.consent.client.ClientFabricServerConsentImpl;

/**
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 */
@ApiStatus.Experimental
public final class ClientFabricServerConsent {
	/**
	 * Returns the most recent list of illegal flags that was sent by the server.
	 *
	 * @return list of illegal flags
	 */
	public static List<Identifier> getIllegalFlags() {
		return Collections.unmodifiableList(ClientFabricServerConsentImpl.illegalFlags);
	}

	/**
	 * Checks for a given flag and mod id whether the flag is illegal.
	 *
	 * @param flag the flag to check against
	 * @return {@code true} if the flag is illegal, {@code false} otherwise
	 */
	public static boolean isIllegal(Identifier flag) {
		for (Identifier illegalFlag : ClientFabricServerConsentImpl.illegalFlags) {
			if (illegalFlag.getNamespace().equals(Flags.COMMON_NAMESPACE)) {
				if (illegalFlag.getPath().equals(flag.getPath())) {
					return true;
				}
			}

			if (illegalFlag.getNamespace().equals(flag.getNamespace())) {
				if (illegalFlag.getPath().equals(Flags.WILDCARD_FEATURE)) {
					return true;
				}

				if (illegalFlag.getPath().equals(flag.getPath())) {
					return true;
				}
			}
		}

		return false;
	}
}
