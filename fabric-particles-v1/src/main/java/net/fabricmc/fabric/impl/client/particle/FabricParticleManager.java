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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;

public final class FabricParticleManager {
	public static final Identifier PARTICLE_ATLAS_TEX = new Identifier("fabric", SpriteAtlasTexture.PARTICLE_ATLAS_TEX.getPath());

	private final VanillaParticleManager manager;

	private final Int2ObjectMap<FabricSpriteProviderImpl> providers = new Int2ObjectOpenHashMap<>();

	final Map<Identifier, List<Identifier>> unloadedSprites = Maps.newConcurrentMap();

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

	public boolean loadParticle(ResourceManager manager, Identifier id, Map<Identifier, List<Identifier>> spritesToLoad) {
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

			unloadedSprites.put(id, spriteIds.stream()
					.map(sprite -> new Identifier(sprite.getNamespace(), "particle/" + sprite.getPath()))
					.collect(Collectors.toList())
			);
		} catch (IOException e) {
			throw new IllegalStateException("(Fabric) Failed to load description for particle " + id, e);
		}

		return true; // i got dis
	}

	public CompletableFuture<Void> reload(CompletableFuture<Void> prev, ResourceReloadListener.Synchronizer sync, ResourceManager manager, Profiler executeProf, Profiler applyProf, Executor one, Executor two) {
		return prev.thenCompose(sync::whenPrepared).thenAcceptAsync(data -> {
			applyProf.startTick();
			applyProf.push("bindSpriteSets");
			List<Sprite> missing = ImmutableList.of(this.manager.getAtlas().getSprite(MissingSprite.getMissingSpriteId()));
			unloadedSprites.forEach((id, sprites) -> {
				getProvider(id).setSprites(sprites.isEmpty() ? missing : getSprites(sprites));
			});
			unloadedSprites.clear();
			applyProf.pop();
			applyProf.endTick();
		}, two);
	}

	private List<Sprite> getSprites(List<Identifier> ids) {
		return ids.stream().map(manager.getAtlas()::getSprite).collect(ImmutableList.toImmutableList());
	}

	private final class FabricSpriteProviderImpl implements FabricSpriteProvider {

		private List<Sprite> sprites = new ArrayList<>();

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
			return sprites;
		}

		public void setSprites(List<Sprite> sprites) {
			this.sprites = sprites;
		}
	}
}
