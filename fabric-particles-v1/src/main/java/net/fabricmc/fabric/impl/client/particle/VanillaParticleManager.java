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

package net.fabricmc.fabric.impl.client.particle;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;

/**
 * Accessors for some private members of ParticleManager.
 *
 * <p>Usage:
 * <pre> {@code
 * SpriteAtlasTexture atlas = ((VanillaParticleManager)MinecraftClient.getInstance().particleManager).getAtlas()
 * }</pre>
 */
public interface VanillaParticleManager {
	SpriteAtlasTexture getAtlas();

	Int2ObjectMap<ParticleFactory<?>> getFactories();
}
