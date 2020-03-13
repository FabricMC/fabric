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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.fabricmc.fabric.impl.client.particle.FabricParticleManager;
import net.fabricmc.fabric.impl.client.particle.VanillaParticleManager;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager implements VanillaParticleManager {
	private final FabricParticleManager fabricParticleManager = new FabricParticleManager(this);

	@Override
	@Accessor("particleAtlasTexture")
	public abstract SpriteAtlasTexture getAtlas();

	@Override
	@Accessor("factories")
	public abstract Int2ObjectMap<ParticleFactory<?>> getFactories();

	@Inject(method = "registerDefaultFactories()V", at = @At("RETURN"))
	private void onRegisterDefaultFactories(CallbackInfo info) {
		fabricParticleManager.injectValues();
	}

	@Inject(method = "loadTextureList(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/Identifier;Ljava/util/Map;)V",
			at = @At("HEAD"),
			cancellable = true)
	private void onLoadTextureList(ResourceManager manager, Identifier id, Map<Identifier, List<Identifier>> spritesToLoad, CallbackInfo info) {
		if (fabricParticleManager.loadParticle(manager, id, spritesToLoad)) {
			info.cancel();
		}
	}

	@Inject(method = "reload("
	        + "Lnet/minecraft/client/resource/ResourceReloadListener$Synchronizer;"
	        + "Lnet/minecraft/client/resource/ResourceManager;"
	        + "Lnet/minecraft/util/profiler/Profiler;"
	        + "Lnet/minecraft/util/profiler/Profiler;"
	        + "Ljava/util/concurrent/Executor;"
	        + "Ljava/util/concurrent/Executor;"
	        + ")Ljava/util/concurrent/CompletableFuture;",
	        at = @At("RETURN"),
	        cancellable = true)
	private void onReload(ResourceReloadListener.Synchronizer sync, ResourceManager manager, Profiler executeProf, Profiler applyProf, Executor one, Executor two, CallbackInfoReturnable<CompletableFuture<Void>> info) {
	    info.setReturnValue(fabricParticleManager.reload(info.getReturnValue(), sync, manager, executeProf, applyProf, one, two));
	}
}
