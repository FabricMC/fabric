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

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.ElementBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.FabricModel;
import net.fabricmc.fabric.api.datagen.v1.model.OverrideBuilder;

@Mixin(Model.class)
public class ModelMixin implements FabricModel {
	@Unique
	private final JsonObject display = new JsonObject();
	@Unique
	private final List<JsonObject> elements = new ObjectArrayList<>();
	@Unique
	private final List<JsonObject> overrides = new ObjectArrayList<>();
	@Unique
	@Nullable
	private GuiLight guiLight = null;
	@Unique
	private boolean ambientOcclusion = true;

	@Override
	public Model withDisplay(DisplayBuilder.Position position, DisplayBuilder builder) {
		this.display.add(position.name().toLowerCase(), builder.build());
		return (Model) (Object) this;
	}

	@Override
	public Model addElement(ElementBuilder builder) {
		this.elements.add(builder.build());
		return (Model) (Object) this;
	}

	@Override
	public Model addOverride(OverrideBuilder builder) {
		this.overrides.add(builder.build());
		return (Model) (Object) this;
	}

	@Override
	public Model setGuiLight(@Nullable GuiLight light) {
		this.guiLight = light;
		return (Model) (Object) this;
	}

	@Override
	public Model setAmbientOcclusion(boolean occlude) {
		this.ambientOcclusion = occlude;
		return (Model) (Object) this;
	}

	@Inject(method = "method_25851", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void addExtraProperties(Map<TextureKey, Identifier> map, CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) {
		if (!display.keySet().isEmpty()) {
			jsonObject.add("display", display);
		}

		if (!elements.isEmpty()) {
			JsonArray elements = new JsonArray();
			this.elements.forEach(elements::add);
			jsonObject.add("elements", elements);
		}

		if (!overrides.isEmpty()) {
			JsonArray overrides = new JsonArray();
			this.overrides.forEach(overrides::add);
			jsonObject.add("overrides", overrides);
		}

		if (guiLight != null) {
			jsonObject.addProperty("gui_light", guiLight.name().toLowerCase());
		}

		if (!ambientOcclusion) {
			jsonObject.addProperty("ambientocclusion", false);
		}
	}

	@Inject(method = "upload(Lnet/minecraft/util/Identifier;Lnet/minecraft/data/client/TextureMap;Ljava/util/function/BiConsumer;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void clearExtraProperties(Identifier id, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, CallbackInfoReturnable<Identifier> cir, Map<TextureKey, Identifier> map) {
		display.keySet().forEach(display::remove);
		elements.clear();
		overrides.clear();
		guiLight = null;
		ambientOcclusion = true;
	}
}
