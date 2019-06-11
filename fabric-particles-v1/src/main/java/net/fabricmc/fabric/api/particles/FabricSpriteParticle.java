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

package net.fabricmc.fabric.api.particles;

import net.fabricmc.fabric.impl.particles.ParticleManagerHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/** Base class for custom particles using Fabric's particle API. */
public abstract class FabricSpriteParticle extends SpriteBillboardParticle {
	public FabricSpriteParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);

		SpriteAtlasTexture sat = ((ParticleManagerHooks)MinecraftClient.getInstance().particleManager).fabric_getSpriteAtlasTexture();
		this.setSprite(sat.getSprite(this.getSprite()));
	}

	/**
	 * Get the identifier of the sprite this particle should use.
	 * This should be the same identifier used to register the sprite with
	 * 	{@link net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback ClientSpriteRegistryCallback}.
	 * 	The particle must be registered under the particle atlas ({@link SpriteAtlasTexture#PARTICLE_ATLAS_TEX}).
	 */
	protected abstract Identifier getSprite();

	public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE; }
}
