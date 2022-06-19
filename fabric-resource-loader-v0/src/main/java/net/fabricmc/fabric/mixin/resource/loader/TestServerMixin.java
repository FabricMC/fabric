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

package net.fabricmc.fabric.mixin.resource.loader;

import java.util.function.Supplier;

import com.mojang.serialization.JsonOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.test.TestServer;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

@Mixin(TestServer.class)
public class TestServerMixin {
	@Inject(method = "method_40379", at = @At("RETURN"), cancellable = true)
	private static void replaceDefaultDataPackSettings(CallbackInfoReturnable<DataPackSettings> cir) {
		cir.setReturnValue(ModResourcePackUtil.createDefaultDataPackSettings());
	}

	@Redirect(method = "method_40377", at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;"))
	@SuppressWarnings("unchecked")
	private static <T> T loadRegistry(Supplier<T> instance, ResourceManager resourceManager) {
		DynamicRegistryManager.Mutable dynamicRegistryManager = DynamicRegistryManager.createAndLoad();
		RegistryOps.ofLoaded(JsonOps.INSTANCE, dynamicRegistryManager, resourceManager);
		return (T) dynamicRegistryManager.toImmutable();
	}
}
