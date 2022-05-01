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

package net.fabricmc.fabric.mixin.resource.loader.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.fabric.impl.resource.loader.client.pack.ProgrammerArtResourcePack;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ClientBuiltinResourcePackProviderMixin {
	@Inject(method = "register", at = @At("RETURN"))
	private void addBuiltinResourcePacks(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory, CallbackInfo ci) {
		// Register mod and built-in resource packs after the vanilla built-in resource packs are registered.
		ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(consumer, factory);
	}

	// ClientBuiltinResourcePackProvider#method_25454 first lambda.
	@Inject(method = "method_25457", at = @At("RETURN"), cancellable = true)
	private static void onSupplyZipProgrammerArtPack(File file, CallbackInfoReturnable<ResourcePack> cir) {
		AbstractFileResourcePack originalPack = (AbstractFileResourcePack) cir.getReturnValue();
		cir.setReturnValue(new ProgrammerArtResourcePack(originalPack, getProgrammerArtModResourcePacks()));
	}

	// ClientBuiltinResourcePackProvider#method_25454 second lambda.
	@Inject(method = "method_25456", at = @At("RETURN"), cancellable = true)
	private static void onSupplyDirProgrammerArtPack(File file, CallbackInfoReturnable<ResourcePack> cir) {
		AbstractFileResourcePack originalPack = (AbstractFileResourcePack) cir.getReturnValue();
		cir.setReturnValue(new ProgrammerArtResourcePack(originalPack, getProgrammerArtModResourcePacks()));
	}

	private static List<ModResourcePack> getProgrammerArtModResourcePacks() {
		List<ModResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, ResourceType.CLIENT_RESOURCES, "programmer_art");
		return packs;
	}
}
