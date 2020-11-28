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

package net.fabricmc.fabric.mixin.registry.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

@Mixin(DynamicRegistryManager.class)
public class DynamicRegistryManagerMixin {
	@Inject(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/RegistryOps$EntryLoader$Impl;<init>()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void onCreateImpl(CallbackInfoReturnable<DynamicRegistryManager.Impl> cir, DynamicRegistryManager.Impl registryManager) {
		DynamicRegistrySetupCallback.EVENT.invoker().onRegistrySetup(registryManager);
	}
}
