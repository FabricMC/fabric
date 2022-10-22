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

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.registry.CombinedDynamicRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

@Mixin(CombinedDynamicRegistries.class)
public class CombinedDynamicRegistriesMixin {
	@Shadow
	@Final
	private DynamicRegistryManager.Immutable combinedRegistryManager;

	@Inject(method = "<init>(Ljava/util/List;Ljava/util/List;)V", at = @At("RETURN"))
	private void init(List list, List list2, CallbackInfo ci) {
		DynamicRegistrySetupCallback.EVENT.invoker().onRegistrySetup(this.combinedRegistryManager);
	}
}
