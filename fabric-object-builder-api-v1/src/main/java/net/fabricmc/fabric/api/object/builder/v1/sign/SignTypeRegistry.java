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

package net.fabricmc.fabric.api.object.builder.v1.sign;

import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

/**
 * This class allows registering {@link SignType}s.
 *
 * <p>A {@link SignType} is used to tell the game what texture a sign should use.
 *
 * <p>These textures are stored under {@code namespace/textures/entity/signs/}.
 */
public final class SignTypeRegistry {
	private SignTypeRegistry() {
	}

	/**
	 * Creates and registers a {@link SignType}.
	 *
	 * @param id the id of this {@link SignType}
	 * @return a new {@link SignType}.
	 */
	public static SignType registerSignType(Identifier id) {
		return SignType.register(new SignType(id.toString()));
	}
}
