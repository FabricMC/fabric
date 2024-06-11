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

package net.fabricmc.fabric.impl.client.model.loading;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public final class ModelLoadingConstants {
	/**
	 * This variant is used to convert user-provided Identifiers for extra models to ModelIdentifiers, since top-level
	 * models that will be baked must have a ModelIdentifier. Models corresponding to the Identifiers will go through
	 * ModelModifier.OnLoad, but models corresponding to the ModelIdentifiers will not.
	 *
	 * <p>This variant must be non-empty, must not contain "=", and must not be equal to "inventory" or "missingno".
	 */
	public static final String RESOURCE_SPECIAL_VARIANT = "fabric_resource";

	private ModelLoadingConstants() {
	}

	public static ModelIdentifier toResourceModelId(Identifier id) {
		return new ModelIdentifier(id, RESOURCE_SPECIAL_VARIANT);
	}

	public static boolean isResourceModelId(ModelIdentifier id) {
		return id.variant().equals(RESOURCE_SPECIAL_VARIANT);
	}
}
