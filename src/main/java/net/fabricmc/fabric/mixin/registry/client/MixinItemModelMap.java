/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.registry.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.registry.ListenableRegistry;
import net.fabricmc.fabric.registry.RegistryListener;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemModels.class)
public class MixinItemModelMap implements RegistryListener<Item> {
	@Shadow
	public Int2ObjectMap<ModelIdentifier> modelIds;
	@Shadow
	private Int2ObjectMap<BakedModel> models;

	private Map<Identifier, ModelIdentifier> fabricModelIdMap;
	private Map<Identifier, BakedModel> fabricModelMap;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(BakedModelManager bakedModelManager, CallbackInfo info) {
		((ListenableRegistry<Item>) Registry.ITEM).registerListener(this);
	}

	@Override
	public void beforeRegistryCleared(Registry<Item> registry) {
		if (fabricModelIdMap == null) {
			fabricModelIdMap = new HashMap<>();
			fabricModelMap = new HashMap<>();
		}

		for (Identifier id : registry.keys()) {
			Item object = registry.get(id);
			int rawId = registry.getRawId(object);
			ModelIdentifier modelId = modelIds.get(rawId);
			BakedModel bakedModel = models.get(rawId);

			if (modelId != null) {
				fabricModelIdMap.put(id, modelId);
			}

			if (bakedModel != null) {
				fabricModelMap.put(id, bakedModel);
			}
		}

		modelIds.clear();
		models.clear();
	}

	@Override
	public void beforeRegistryRegistration(Registry<Item> registry, int id, Identifier identifier, Item object, boolean isNew) {
		if (fabricModelIdMap != null && fabricModelIdMap.containsKey(identifier)) {
			modelIds.put(id, fabricModelIdMap.get(identifier));
		}

		if (fabricModelMap != null && fabricModelMap.containsKey(identifier)) {
			models.put(id, fabricModelMap.get(identifier));
		}
	}

}
