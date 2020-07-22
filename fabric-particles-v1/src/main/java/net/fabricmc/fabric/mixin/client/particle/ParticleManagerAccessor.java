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

package net.fabricmc.fabric.mixin.client.particle;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
	@Accessor("particleAtlasTexture")
	SpriteAtlasTexture getParticleAtlasTexture();

	@Accessor("factories")
	Int2ObjectMap<ParticleFactory<?>> getFactories();

	// NOTE: The field signature is actually Map<Identifier, SimpleSpriteProvider>
	// This still works due to type erasure
	@Accessor("spriteAwareFactories")
	Map<Identifier, SpriteProvider> getSpriteAwareFactories();

	@Mixin(targets = "net/minecraft/client/particle/ParticleManager$SimpleSpriteProvider")
	interface SimpleSpriteProviderAccessor {
		@Accessor("sprites")
		List<Sprite> getSprites();
	}
}
