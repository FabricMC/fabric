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

package net.fabricmc.fabric.mixin.client.particles;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.impl.particles.ParticleFactoryRegistryImpl;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {

    @Shadow @Final
    private Map field_18300;

    @Shadow @Final
    private Int2ObjectMap<ParticleFactory<?>> factories;

    @Inject(method = "registerDefaultFactories()V", at = @At("RETURN"))
    private void onRegisterDefaultFactories() {
        ParticleFactoryRegistryImpl.INSTANCE.injectValues(factories, field_18300);
    }
}
