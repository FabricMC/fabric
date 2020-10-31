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

package net.fabricmc.fabric.mixin.generatortype;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.world.gen.GeneratorOptions;

import net.fabricmc.fabric.impl.generatortype.FabricGeneratorTypeImpl.ClientGeneratorType;

@Mixin(MoreOptionsDialog.class)
public final class MoreOptionsDialogMixin {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Shadow
	private Optional<GeneratorType> field_25049;

	@Shadow
	private GeneratorOptions generatorOptions;

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_28087", at = @At("HEAD"))
	private void openFabricCustomizationScreen(MinecraftClient client, CreateWorldScreen parent, ButtonWidget customizeTypeButton, CallbackInfo ci) {
		if (this.field_25049.isPresent()) {
			GeneratorType.ScreenProvider screenProvider = ((ClientGeneratorType) this.field_25049.get()).getCustomizationScreenFactory();

			if (screenProvider != null) {
				client.openScreen(screenProvider.createEditScreen(parent, this.generatorOptions));
			}
		}
	}

	@Redirect(method = "setVisible", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	private boolean setCustomizationButtonVisibility(Map<Optional<GeneratorType>, GeneratorType.ScreenProvider> screenProviderMap, Object object) {
		if (screenProviderMap.containsKey(this.field_25049)) {
			return true;
		}

		if (this.field_25049.isPresent()) {
			GeneratorType generatorType = this.field_25049.get();

			if (generatorType instanceof ClientGeneratorType) {
				return ((ClientGeneratorType) generatorType).getCustomizationScreenFactory() != null;
			}
		}

		return false;
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Redirect(method = "method_28093", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;isDebugWorld()Z"))
	private boolean isDebugGeneratorType(GeneratorOptions generatorOptions) {
		if (this.field_25049.isPresent()) {
			GeneratorType generatorType = this.field_25049.get();

			if (generatorType instanceof ClientGeneratorType) {
				return ((ClientGeneratorType) generatorType).isDebugGeneratorType();
			}
		}

		return generatorOptions.isDebugWorld();
	}
}
