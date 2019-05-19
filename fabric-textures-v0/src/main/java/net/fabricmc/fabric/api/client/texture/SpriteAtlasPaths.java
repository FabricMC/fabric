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

package net.fabricmc.fabric.api.client.texture;

/**
 * Constants for vanilla atlas path keys, used for discerning between texture atlases.
 *
 * @see net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
 */
public final class SpriteAtlasPaths {
	public static final String BLOCK = "textures";
	public static final String PARTICLE = "textures/particle";
	public static final String PAINTING = "textures/painting";
	public static final String MOB_EFFECT = "textures/mob_effect";

	private SpriteAtlasPaths() {

	}
}
