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

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleEffect.Factory;

/**
 * Methods for creating particle types, both simple and using an existing attribute factory.
 */
public final class FabricParticleTypes {

    /**
     * Creates a new, default particle type for the given id.
     *
     * @param id The particle id.
     */
    public static DefaultParticleType simple() {
        return simple(false);
    }

    /**
     * Creates a new, default particle type for the given id.
     *
     * @param id The particle id.
     * @param alwaysSpawn True to always spawn the particle regardless of distance.
     */
    public static DefaultParticleType simple(boolean alwaysSpawn) {
        return new Simple(alwaysSpawn);
    }

    /**
     * Creates a new particle type with a custom factory for packet/data serialization.
     *
     * @param id The particle id.
     * @param factory     A factory for serializing packet data and string command parameters into a particle effect.
     */
    public static <T extends ParticleEffect> ParticleType<T> complex(ParticleEffect.Factory<T> factory) {
        return complex(false, factory);
    }

    /**
     * Creates a new particle type with a custom factory for packet/data serialization.
     *
     * @param id The particle id.
     * @param alwaysSpawn True to always spawn the particle regardless of distance.
     * @param factory     A factory for serializing packet data and string command parameters into a particle effect.
     */
    public static <T extends ParticleEffect> ParticleType<T> complex(boolean alwaysSpawn, ParticleEffect.Factory<T> factory) {
        return new Complex<>(alwaysSpawn, factory);
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
