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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;

public final class FabricParticleManager {
	private final VanillaParticleManager manager;

	private final Int2ObjectMap<FabricSpriteProviderImpl> providers = new Int2ObjectOpenHashMap<>();

	public FabricParticleManager(VanillaParticleManager manager) {
		this.manager = manager;
	}

	public void injectValues() {
		manager.getFactories().putAll(ParticleFactoryRegistryImpl.INSTANCE.factories);
		ParticleFactoryRegistryImpl.INSTANCE.constructors.forEach((id, factory) -> {
			FabricSpriteProviderImpl provider = new FabricSpriteProviderImpl();

			providers.put((int) id, provider);
			manager.getFactories().put((int) id, factory.create(provider));
		});
	}

	private FabricSpriteProviderImpl getProvider(Identifier id) {
		if (!ParticleFactoryRegistryImpl.INSTANCE.constructorsIdsMap.containsKey(id)) {
			return null;
		}

		return providers.get((int) ParticleFactoryRegistryImpl.INSTANCE.constructorsIdsMap.get(id));
	}

	public boolean loadParticle(ResourceManager manager, Identifier id) {
		FabricSpriteProviderImpl provider = getProvider(id);

		if (provider == null) {
			return false; // preserve vanilla behaviour (i don't got dis)
		}

		Identifier file = new Identifier(id.getNamespace(), "particles/" + id.getPath() + ".json");

		try (Reader reader = new InputStreamReader(manager.getResource(file).getInputStream(), StandardCharsets.UTF_8)) {
			List<Identifier> spriteIds = ParticleTextureData.load(JsonHelper.deserialize(reader)).getTextureList();

			if (spriteIds == null) {
				// Particles should have a list of picks, even if it's just empty.
				throw new IllegalStateException("(Fabric) Missing texture list for particle " + id);
			}

			provider.setSprites(spriteIds);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load description for particle " + id, e);
		}

		return true; // i got dis
	}

	private final class FabricSpriteProviderImpl implements FabricSpriteProvider {
		private List<Identifier> spriteIds;

		// @Nullable
		private List<Sprite> sprites;

		@Override
		public Sprite getSprite(int min, int max) {
			return getSprites().get(min * (getSprites().size() - 1) / max);
		}

		@Override
		public Sprite getSprite(Random random_1) {
			return getSprites().get(random_1.nextInt(getSprites().size()));
		}

		@Override
		public SpriteAtlasTexture getAtlas() {
			return manager.getAtlas();
		}

		@Override
		public List<Sprite> getSprites() {
			if (sprites == null) {
				sprites = spriteIds.stream().map(getAtlas()::getSprite).collect(Collectors.toList());
			}

			return sprites;
		}

		public void setSprites(List<Identifier> sprites) {
			this.sprites = null;
			this.spriteIds = ImmutableList.copyOf(sprites);
		}
	}
}
