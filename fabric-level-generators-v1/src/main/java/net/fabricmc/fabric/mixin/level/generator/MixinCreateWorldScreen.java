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

package net.fabricmc.fabric.mixin.level.generator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.level.generator.v1.FabricLevelGeneratorType;

@Environment(EnvType.CLIENT)
@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen extends Screen {
	@Shadow
	private int generatorType;

	protected MixinCreateWorldScreen(Text title) {
		super(title);
	}

	@Inject(method = "method_19926", at = @At("HEAD"))
	void customizeLevelGeneratorType(ButtonWidget buttonWidget, CallbackInfo ci) {
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[generatorType];

		if (levelGeneratorType instanceof FabricLevelGeneratorType && levelGeneratorType.isCustomizable()) {
			//noinspection ConstantConditions
			minecraft.openScreen(((FabricLevelGeneratorType) levelGeneratorType).customizationScreen((CreateWorldScreen) (Object) this));
		}
	}
}
