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

package net.fabricmc.fabric.impl.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/**
 * Various hooks into {@link net.minecraft.client.particle.ParticleManager ParticleManager} for registering particles.
 * You shouldn't generally need to use this directly, these methods are called by {@link net.fabricmc.fabric.api.particles.ParticleRegistry}.
 */
public interface ParticleManagerHooks {
	/** Get the sprite atlas texture used by {@link net.minecraft.client.particle.ParticleManager}. */
	SpriteAtlasTexture fabric_getSpriteAtlasTexture();

	/** Register a custom {@link ParticleFactory} for the given {@link ParticleType}. */
	@Environment(EnvType.CLIENT)
	<T extends ParticleEffect> void fabric_registerCustomFactory(ParticleType<T> pt, ParticleFactory<T> pf);
}
