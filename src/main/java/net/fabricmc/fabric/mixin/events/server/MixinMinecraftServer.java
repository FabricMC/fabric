/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.events.server;

import net.fabricmc.fabric.events.ServerEvent;
import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;method_3791(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "run")
	public void afterSetupServer(CallbackInfo info) {
		for (Consumer<MinecraftServer> handler : ((HandlerArray<Consumer<MinecraftServer>>) ServerEvent.START).getBackingArray()) {
			handler.accept((MinecraftServer) (Object) this);
		}
	}

	@Inject(at = @At("HEAD"), method = "shutdown")
	public void beforeShutdownServer(CallbackInfo info) {
		for (Consumer<MinecraftServer> handler : ((HandlerArray<Consumer<MinecraftServer>>) ServerEvent.STOP).getBackingArray()) {
			handler.accept((MinecraftServer) (Object) this);
		}
	}
}
