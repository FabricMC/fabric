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

package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.server.ServerReloadCallback;
import net.fabricmc.fabric.api.event.server.ServerSaveCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "run")
	public void afterSetupServer(CallbackInfo info) {
		ServerStartCallback.EVENT.invoker().onStartServer((MinecraftServer) (Object) this);
	}

	@Inject(method = "reload()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackContainerManager;callCreators()V"))
	public void beforeReload(CallbackInfo info) {
		ServerReloadCallback.PRE_EVENT.invoker().onReload((MinecraftServer) (Object) this);
	}

	@Inject(method = "reload()V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/PlayerManager;onDataPacksReloaded()V"))
	public void afterReload(CallbackInfo info) {
		ServerReloadCallback.POST_EVENT.invoker().onReload((MinecraftServer) (Object) this);
	}

	@Inject(method = "save(ZZZ)Z", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onSave(CallbackInfoReturnable<Boolean> info, boolean silent, boolean flush, boolean enforced, boolean iteratedWorlds, ServerWorld overworld, LevelProperties mainLevelProperties) {
		ServerSaveCallback.EVENT.invoker().onSave((MinecraftServer) (Object) this, silent, flush, enforced);
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
