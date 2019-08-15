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

import net.fabricmc.fabric.api.particles.ParticleTypeRegistry;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleEffect.Factory;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleTypeRegistryImpl implements ParticleTypeRegistry {

    public static final ParticleTypeRegistryImpl INSTANCE = new ParticleTypeRegistryImpl();

    private ParticleTypeRegistryImpl() { }

    @Override
    public DefaultParticleType register(Identifier id, boolean alwaysSpawn) {
        return Registry.register(Registry.PARTICLE_TYPE, id, new Simple(alwaysSpawn));
    }

    @Override
    public <T extends ParticleEffect> ParticleType<T> register(Identifier id, boolean alwaysSpawn, ParticleEffect.Factory<T> factory) {
        return Registry.register(Registry.PARTICLE_TYPE, id.toString(), new Complex<>(alwaysSpawn, factory));
    }

    // Constructor is (gasp!) protected
    public static class Simple extends DefaultParticleType {
        public Simple(boolean alwaysSpawn) {
            super(alwaysSpawn);
        }
    }

    // Same for this
    public static class Complex<T extends ParticleEffect> extends ParticleType<T> {
        public Complex(boolean alwaysSpawn, Factory<T> factory) {
            super(alwaysSpawn, factory);
        }
    }
}
