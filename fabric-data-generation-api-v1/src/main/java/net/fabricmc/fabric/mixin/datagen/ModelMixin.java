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

package net.fabricmc.fabric.mixin.datagen;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.data.client.Model;

import net.fabricmc.fabric.api.datagen.v1.FabricModel;

@Mixin(Model.class)
public class ModelMixin implements FabricModel {
	@Unique
	private GUILight guiLight;
	@Unique
	private boolean ambientOcclusion;

	@Override
	public Model setGUILight(GUILight light) {
		this.guiLight = light;
		return (Model) (Object) this;
	}

	@Override
	public Model setAmbientOcclusion(boolean occlude) {
		this.ambientOcclusion = occlude;
		return (Model) (Object) this;
	}

	@Inject(method = "method_25851", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void addGUILight(Map map, CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) {
		if (guiLight != null) {
			jsonObject.addProperty("gui_light", guiLight.name().toLowerCase());
		}

		if (!ambientOcclusion) {
			jsonObject.addProperty("ambientocclusion", false);
		}
	}
}
