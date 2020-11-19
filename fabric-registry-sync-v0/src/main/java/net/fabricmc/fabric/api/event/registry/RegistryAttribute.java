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

package net.fabricmc.fabric.api.event.registry;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.registry.v1.RegistryAttributes;

public enum RegistryAttribute {
	/**
	 * Registry will be saved to disk when modded.
	 */
	// TODO: Refer to constant in registry-sync-v1
	PERSISTED(new Identifier("fabric-registry-api-v1", "persisted")),

	/**
	 * Registry will be synced to the client when modded.
	 */
	// TODO: Refer to constant in registry-sync-v1
	SYNCED(new Identifier("fabric-registry-api-v1", "synced")),

	/**
	 * Registry has been modded.
	 */
	MODDED(RegistryAttributes.MODDED);

	private final Identifier newKey;

	RegistryAttribute(Identifier newKey) {
		this.newKey = newKey;
	}

	/**
	 * For implementation purposes only!
	 * Returned key values are subject to change.
	 */
	@Deprecated
	@ApiStatus.Internal
	public Identifier getNewKey() {
		return this.newKey;
	}
}
