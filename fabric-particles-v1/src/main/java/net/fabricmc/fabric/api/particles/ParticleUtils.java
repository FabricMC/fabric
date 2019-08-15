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

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Utility class for spawning particles.
 */
public final class ParticleUtils {

    private ParticleUtils() { }

    /**
     * Adds a particle to the world with a pre-determined position and velocity.
     *
     * @param w             The world
     * @param effect        Data packet describing the particle to spawn
     * @param pos           Position
     * @param vel           Velocity
     */
    public static void spawnParticle(World w, ParticleEffect effect, Vec3d pos, Vec3d vel) {
        spawnParticle(w, effect, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
    }

    /**
     * Adds a particle to the world with a pre-determined position and velocity.
     */
    public static void spawnParticle(World w, ParticleEffect particleId, Vec3d pos, double speedX, double speedY, double speedZ) {
        spawnParticle(w, particleId, pos.x, pos.y, pos.z, speedX, speedY, speedZ);
    }

    /**
     * Adds a particle to the world with a pre-determined position and velocity.
     */
    public static void spawnParticle(World w, ParticleEffect particleId, double posX, double posY, double posZ, double speedX, double speedY, double speedZ) {
        w.addParticle(particleId, posX, posY, posZ, speedX, speedY, speedZ);
    }
}
