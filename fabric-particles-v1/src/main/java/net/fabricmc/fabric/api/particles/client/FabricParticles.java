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

package net.fabricmc.fabric.api.particles.client;

import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.impl.particles.FabricParticlesImpl;
import net.fabricmc.fabric.impl.particles.ParticleManagerHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

/**
 * Core methods for registering and displaying particles with the Fabric API.
 */
public interface FabricParticles {
	FabricParticles INSTANCE = new FabricParticlesImpl();

	/**
	 * Retrieve the appropriate sprite atlas for particle textures.
	 * <br><br>
	 * Use this along with {@link SpriteAtlasTexture#getSprite(Identifier)} and
	 * 	{@link SpriteBillboardParticle#setSprite(Sprite)}
	 * 	to apply a texture to your particle. You'll also need to register the sprite with
	 * 	{@link ClientSpriteRegistryCallback}.
	 */
	static SpriteAtlasTexture getParticleSpriteAtlas() {
		return ((ParticleManagerHooks)MinecraftClient.getInstance().particleManager).fabric_getSpriteAtlasTexture();
	}

	/**
	 * Register a {@link ParticleFactory} for the given {@link ParticleType}.
	 *
	 * @param type The type to register a factory for.
	 * @param factory The factory method.
	 */
	<T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, ParticleFactory<T> factory);
}
