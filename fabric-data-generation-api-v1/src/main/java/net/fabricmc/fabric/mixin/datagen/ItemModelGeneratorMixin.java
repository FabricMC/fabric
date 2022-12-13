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

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.ElementBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.FabricItemModelGenerator;
import net.fabricmc.fabric.api.datagen.v1.model.OverrideBuilder;

@Mixin(ItemModelGenerator.class)
public class ItemModelGeneratorMixin implements FabricItemModelGenerator {
	@Shadow
	@Final
	public BiConsumer<Identifier, Supplier<JsonElement>> writer;

	@Override
	public void register(Item item, Model model, TextureMap textureMap) {
		model.upload(ModelIds.getItemModelId(item), textureMap, this.writer);
	}

	@Override
	public void register(Item item, String suffix, Model model, TextureMap textureMap) {
		model.upload(ModelIds.getItemSubModelId(item, suffix), textureMap, this.writer);
	}

	@Override
	public void register(Item item, Model model, TextureMap textureMap, ElementBuilder... elements) {
		register(item, model, textureMap, List.of(elements), Collections.emptyList());
	}

	@Override
	public void register(Item item, Model model, TextureMap textureMap, OverrideBuilder... overrides) {
		register(item, model, textureMap, Collections.emptyList(), List.of(overrides));
	}

	@Override
	public void register(Item item, Model model, TextureMap textureMap, List<ElementBuilder> elements, List<OverrideBuilder> overrides) {
		elements.forEach(model::addElement);
		overrides.forEach(model::addOverride);
		model.upload(ModelIds.getItemModelId(item), textureMap, this.writer);
	}
}
