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

package net.fabricmc.fabric.api.server.consent.v1;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl;

/**
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 */
@ApiStatus.Experimental
public final class FabricServerConsent {
	/**
	 * Returns whether the Fabric Server Consent API is enabled on this server.
	 *
	 * @return {@code true} if it is enabled, {@code false} otherwise
	 */
	public static boolean isEnabled() {
		return FabricServerConsentImpl.enabled;
	}

	/**
	 * The list of illegal flags as specified by the consents file.
	 *
	 * @return the list of illegal flags
	 */
	public static List<Identifier> getIllegalFlags() {
		return Collections.unmodifiableList(FabricServerConsentImpl.illegalFlags);
	}
}
