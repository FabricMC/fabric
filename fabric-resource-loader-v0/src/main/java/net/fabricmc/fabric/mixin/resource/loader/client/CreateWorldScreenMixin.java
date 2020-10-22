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

import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.mixin.resource.loader.ResourcePackManagerAccessor;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
	@Shadow
	private ResourcePackManager field_25792;

	@Inject(method = "method_30296", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;scanPacks()V", shift = At.Shift.BEFORE))
	private void onScanPacks(CallbackInfoReturnable<Pair<File, ResourcePackManager>> cir) {
		// Allow to display built-in data packs in the data pack selection screen at world creation.
		((ResourcePackManagerAccessor) this.field_25792).getProviders().add(new ModResourcePackCreator(ResourceType.SERVER_DATA));
	}

	@ModifyArg(method = "method_31130", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;<init>(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/resource/DataPackSettings;Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;)V"), index = 1)
	private static DataPackSettings onNew(DataPackSettings settings) {
		ModResourcePackCreator modResourcePackCreator = new ModResourcePackCreator(ResourceType.SERVER_DATA);
		List<ResourcePackProfile> moddedResourcePacks = new ArrayList<>();
		modResourcePackCreator.register(moddedResourcePacks::add, ResourcePackProfile::new);

		List<String> enabled = new ArrayList<>(settings.getEnabled());
		List<String> disabled = new ArrayList<>(settings.getDisabled());

		// This ensure that any built-in registered data packs by mods which needs to be enabled by default are
		// as the data pack screen automatically put any data pack as disabled except the Default data pack.
		for (ResourcePackProfile profile : moddedResourcePacks) {
			ResourcePack pack = profile.createResourcePack();

			if (pack instanceof ModNioResourcePack && ((ModNioResourcePack) pack).shouldBeEnabledByDefault()) {
				enabled.add(profile.getName());
			} else {
				disabled.add(profile.getName());
			}
		}

		return new DataPackSettings(enabled, disabled);
	}
}
