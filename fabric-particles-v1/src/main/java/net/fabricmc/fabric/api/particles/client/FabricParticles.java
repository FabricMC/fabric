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
 * Core methods for registering and displaying particles with the Fabric API. Example:
 * <br><br>
 * <pre><code>
 * public class ParticleExample implements ModInitializer, ClientModInitializer {
 *   // Create a ParticleType for our custom particle
 *   private static final DefaultParticleType exampleParticleType = FabricParticleTypes.createSimpleParticleType();
 *   // The ID of our particle, for repeated use in registry
 *   private static final Identifier exampleParticleID = new Identifier("particleexample:example_particle");
 *
 *   public void onInitialize() {
 *     // Register our particle type
 *     Registry.register(Registry.PARTICLE_TYPE, exampleParticleID, exampleParticleType);
 *   }
 *
 *  {@literal @}Environment(EnvType.CLIENT)
 *   public void onInitializeClient() {
 *     // Register the sprite for our particle
 *     ClientSpriteRegistryCallback.event(SpriteAtlasTexture.PARTICLE_ATLAS_TEX).register((atlasTexture, registry) -> {
 *       registry.register(exampleParticleID);
 *     });
 *
 *     // Register a ParticleFactory for our custom particle
 *     FabricParticles.INSTANCE.registerParticleFactory(exampleParticleType, (type, world, x, y, z, vx, vy, vz) ->
 *       new ExampleParticle(world, x, y, z, vx, vy, vz));
 *   }
 *
 *   // Implement our custom particle
 *  {@literal @}Environment(EnvType.CLIENT)
 *   static class ExampleParticle extends SpriteBillboardParticle {
 *     ExampleParticle(World w, double x, double y, double z, double vx, double vy, double vz) {
 *       super(w, x, y, z, vx, vy, vz);
 *       this.setSprite(FabricParticles.getParticleSpriteAtlas().getSprite(exampleParticleID));
 *     }
 *
 *     public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE; }
 *   }
 * }
 * </code></pre>
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
