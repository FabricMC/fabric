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

import java.util.Map;

import com.google.gson.Gson;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.particles.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleFactoryRegistryImpl implements ParticleFactoryRegistry {

    public static final ParticleFactoryRegistryImpl INSTANCE = new ParticleFactoryRegistryImpl();

    private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<PendingParticleFactory<?>> constructors = new Int2ObjectOpenHashMap<>();

    private ParticleFactoryRegistryImpl() { }

    @Override
    public <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
        factories.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
    }

    @Override
    public <T extends ParticleEffect> void register(ParticleType<T> type, PendingParticleFactory<T> factory) {
        constructors.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
    }

    public void injectValues(Int2ObjectMap<ParticleFactory<?>> factories, Map<Identifier, SpriteProvider> spriteProviders) {
        factories.putAll(this.factories);

        constructors.forEach((id, factory) -> {
            SpriteProvider spriteProvider = AccessHack.createSimpleSpriteProvider();

            spriteProviders.put(Registry.PARTICLE_TYPE.getId(Registry.PARTICLE_TYPE.get(id)), spriteProvider);
            factories.put((int)id, factory.create(spriteProvider));
        });
    }

    /**
     * Uses reflection to obtain new instances of the private inner class `ParticleManager.SimpleSpriteProvider`.
     */
    static final class AccessHack {
        private static final Object dummy = new Object();
        private static final Gson gson = new Gson();

        private static Class<?> cachedResponse;
        private static Class<?> getSimpleSpriteProviderClass() {
            if (cachedResponse != null) {
                return cachedResponse;
            }
            for (Class<?> cls : ParticleManager.class.getDeclaredClasses()) {
                if (SpriteProvider.class.isAssignableFrom(cls)) {
                    return cachedResponse = cls;
                }
            }
            throw new IllegalStateException("net.minecraft.client.particle.ParticleManager.SimpleSpriteProvider is gone!");
        }

        // Gson makes for a crude, yet effective and convenient reflective instantiator.
        static SpriteProvider createSimpleSpriteProvider() {
            return (SpriteProvider)gson.fromJson(gson.toJson(dummy), getSimpleSpriteProviderClass());
        }
    }
}
