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

package net.fabricmc.fabric.mixin.tag.extension;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.impl.tag.extension.TagFactoryImpl;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_29440", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ServerResourceManager;loadRegistryTags()V", shift = At.Shift.AFTER))
	private void method_29440(Collection<?> collection, ServerResourceManager serverResourceManager, CallbackInfo ci) {
		// Load dynamic registry tags on datapack reload.
		TagFactoryImpl.loadDynamicRegistryTags((MinecraftServer) (Object) (this));
	}
}
