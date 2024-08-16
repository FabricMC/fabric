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

package net.fabricmc.fabric.mixin.event.lifecycle.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientTagLoaderMixin {
	@Shadow
	@Final
	private DynamicRegistryManager.Immutable combinedDynamicRegistries;

	@Inject(method = "onSynchronizeTags", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FuelRegistry;createDefault(Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;Lnet/minecraft/resource/featuretoggle/FeatureSet;)Lnet/minecraft/item/FuelRegistry;"))
	private void invokeTagsLoaded(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
		CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(combinedDynamicRegistries, true);
	}
}
