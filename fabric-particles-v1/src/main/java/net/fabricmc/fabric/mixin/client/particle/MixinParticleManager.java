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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.ParticleManager;

import net.fabricmc.fabric.impl.client.particle.ParticleFactoryRegistryImpl;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {
	@Inject(method = "registerDefaultFactories()V", at = @At("RETURN"))
	private void onRegisterDefaultFactories(CallbackInfo info) {
		ParticleFactoryRegistryImpl.INSTANCE.initialize((ParticleManager) (Object) this);
	}
}
