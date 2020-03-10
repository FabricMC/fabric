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

package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessItemRenderer;

public class IndigoQuadHandler implements ItemRenderContext.VanillaQuadHandler {
	private final AccessItemRenderer itemRenderer;

	public IndigoQuadHandler(AccessItemRenderer itemRenderer) {
		this.itemRenderer = itemRenderer;
	}

	@Override
	public void accept(BakedModel model, ItemStack stack, int color, int overlay, MatrixStack matrixStack, VertexConsumer buffer) {
		itemRenderer.fabric_renderBakedItemModel(model, stack, color, overlay, matrixStack, buffer);
	}
}
