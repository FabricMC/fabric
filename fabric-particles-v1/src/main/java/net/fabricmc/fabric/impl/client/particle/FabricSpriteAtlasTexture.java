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

import java.util.Collection;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class FabricSpriteAtlasTexture extends SpriteAtlasTexture {
	private final FabricParticleManager particleManager;

	public FabricSpriteAtlasTexture(Identifier id, FabricParticleManager particleManager) {
		super(id);
		this.particleManager = particleManager;
	}

	@Override
	public SpriteAtlasTexture.Data stitch(ResourceManager manager, Stream<Identifier> sprites, Profiler profiler, int z) {
		return super.stitch(manager, Streams.concat(sprites, particleManager.unloadedSprites.values().stream().flatMap(Collection::stream)), profiler, z);
	}
}
