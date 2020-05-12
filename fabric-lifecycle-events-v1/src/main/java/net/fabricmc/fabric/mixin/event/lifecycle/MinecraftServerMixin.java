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

package net.fabricmc.fabric.mixin.event.lifecycle;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "run")
	public void afterSetupServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_START.invoker().onChangeLifecycle((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("HEAD"), method = "shutdown")
	public void beforeShutdownServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_STOPPING.invoker().onChangeLifecycle((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("TAIL"), method = "shutdown")
	public void afterShutdownServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_STOPPED.invoker().onChangeLifecycle((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("RETURN"), method = "tick")
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		ServerLifecycleEvents.SERVER_TICK.invoker().onTick((MinecraftServer) (Object) this);
	}
}
