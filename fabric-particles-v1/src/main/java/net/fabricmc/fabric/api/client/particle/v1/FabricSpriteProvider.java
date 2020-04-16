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

package net.fabricmc.fabric.api.client.particle.v1;

import java.util.List;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.ParticleType;

/**
 * FabricSpriteProvider. It does the same thing as vanilla's SpriteProvider,
 * but in a way that's accessible to mods, and that exposes the atlas as well.
 *
 * <p>Custom sprites registered using ParticleFactoryRegistry have the options
 * to supply a particle factory which will recieve an instance of this
 * interface containing the sprites set loaded for their particle from the
 * active resourcepacks.
 *
 * @see ParticleFactoryRegistry#register(ParticleType, ParticleFactory)
 * @see ParticleFactoryRegistry.PendingParticleFactory
 */
public interface FabricSpriteProvider extends SpriteProvider {
	/**
	 * Returns the entire particles texture atlas.
	 */
	SpriteAtlasTexture getAtlas();

	/**
	 * Gets the list of all sprites available for this particle to use.
	 * This is defined in your resourcepack.
	 */
	List<Sprite> getSprites();
}
