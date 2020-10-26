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

package net.fabricmc.fabric.test.rendering.client;

import java.util.Collections;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.test.rendering.CustomArmorTests;

@Environment(EnvType.CLIENT)
public class CustomArmorTestsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CustomArmorModel model = new CustomArmorModel(1.0F);
		ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> model, CustomArmorTests.customModeledArmor);
		ArmorRenderingRegistry.registerTexture((entity, stack, slot, secondLayer, suffix, defaultTexture) ->
				new Identifier("fabric-rendering-v1-testmod", "textures/cube.png"), CustomArmorTests.customModeledArmor);

		ArmorRenderingRegistry.registerTexture((entity, stack, slot, secondLayer, suffix, defaultTexture) ->
				new Identifier("fabric-rendering-v1-testmod", "textures/custom_texture.png"), CustomArmorTests.customTexturedArmor);

		ArmorRenderingRegistry.registerSimpleTexture(new Identifier("fabric-rendering-v1-testmod", "simple_textured_armor"), CustomArmorTests.simpleTexturedArmor);
	}

	private static class CustomArmorModel extends BipedEntityModel<LivingEntity> {
		private final ModelPart part;

		CustomArmorModel(float scale) {
			super(scale, 0, 1, 1);
			part = new ModelPart(this, 0, 0);
			part.addCuboid(-5F, 0F, 2F, 10, 10, 10);
			part.setPivot(0F, 0F, 0F);
			part.mirror = true;
		}

		@Override
		protected Iterable<ModelPart> getBodyParts() {
			return Collections.singleton(part);
		}

		@Override
		protected Iterable<ModelPart> getHeadParts() {
			return Collections::emptyIterator;
		}
	}
}
