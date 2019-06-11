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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.particles.ParticleRegistryImpl;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public interface ParticleRegistry {
	ParticleRegistry INSTANCE = new ParticleRegistryImpl();

	DefaultParticleType createSimpleParticleType();
	DefaultParticleType createSimpleParticleType(boolean shouldAlwaysSpawn);

	<T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory);
	<T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory, boolean shouldAlwaysSpawn);

	@Environment(EnvType.CLIENT)
	<T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, ParticleFactory<T> factory);
}
