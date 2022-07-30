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

import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "method_29741")
	public void afterSetupServer(CallbackInfo info) {
		ServerStartCallback.EVENT.invoker().onStartServer((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("HEAD"), method = "shutdown")
	public void beforeShutdownServer(CallbackInfo info) {
		ServerStopCallback.EVENT.invoker().onStopServer((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("RETURN"), method = "tick")
	protected void tick(BooleanSupplier var1, CallbackInfo info) {
		ServerTickCallback.EVENT.invoker().tick((MinecraftServer) (Object) this);
	}
}
