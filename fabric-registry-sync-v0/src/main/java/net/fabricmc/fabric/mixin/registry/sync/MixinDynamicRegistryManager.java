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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.impl.registry.sync.DynamicRegistrySync;

@Mixin(DynamicRegistryManager.class)
public class MixinDynamicRegistryManager {
	// This is the "template" for all subsequent built-in dynamic registry managers,
	// but it still contains the same objects as BuiltinRegistries, while the subsequent
	// managers built from this template will contain copies.
	@Shadow
	private static DynamicRegistryManager.Impl BUILTIN;

	/**
	 * Ensures that any registrations made into {@link net.minecraft.util.registry.BuiltinRegistries} after
	 * {@link DynamicRegistryManager} has been class-loaded are still propagated.
	 */
	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void setupBuiltInSync(CallbackInfo ci) {
		DynamicRegistrySync.setupSync(BUILTIN);
	}
}
