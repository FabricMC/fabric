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

package net.fabricmc.fabric.impl.extensibility.item.v1;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.extensibility.item.v1.FabricTrident;

/**
 * This is the default implementation for FabricTrident, allowing for the easy creation of new tridents with no new modded functionality.
 */
public class FabricTridentItem extends TridentItem implements FabricTrident {
	private static final TridentEntityModel DEFAULT_TRIDENT_ENTITY_MODEL = new TridentEntityModel();
	private static final Identifier DEFAULT_TEXTURE = TridentEntityModel.TEXTURE;

	private final Identifier tridentEntityIdentifier;

	public FabricTridentItem(Settings settings) {
		this(settings, DEFAULT_TRIDENT_ENTITY_MODEL, DEFAULT_TEXTURE);
	}

	public FabricTridentItem(Settings settings, Model tridentModel, Identifier tridentEntityTexture) {
		super(settings);
		BuiltinItemRendererRegistry.INSTANCE.register(this, (ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) -> {
			matrices.push();
			matrices.scale(1.0F, -1.0F, -1.0F);
			VertexConsumer vertexConsumer2 = ItemRenderer.getDirectGlintVertexConsumer(vertexConsumers, tridentModel.getLayer(tridentEntityTexture), false, stack.hasGlint());
			tridentModel.render(matrices, vertexConsumer2, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
			matrices.pop();
		});
		this.tridentEntityIdentifier = tridentEntityTexture;
	}

	@Override
	public ModelIdentifier getInventoryModelIdentifier() {
		// super hacky, probably a better way but it works
		return new ModelIdentifier(Registry.ITEM.getId(this).toString() + "#inventory");
	}

	@Override
	public ModelIdentifier getTridentInHandModelIdentifier() {
		// same as above, need to think of something better
		return new ModelIdentifier(Registry.ITEM.getId(this).toString() + "_in_hand#inventory");
	}

	@Override
	public Identifier getEntityTexture() {
		return this.tridentEntityIdentifier;
	}
}
