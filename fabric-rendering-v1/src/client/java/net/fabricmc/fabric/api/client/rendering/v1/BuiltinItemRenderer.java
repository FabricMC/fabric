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

package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Builtin item renderers render items with custom code.
 * They allow using non-model rendering, such as BERs, for items.
 *
 * <p>An item with a builtin renderer must have a model extending {@code minecraft:builtin/entity}.
 * The renderers are registered with {@link BuiltinItemRendererRegistry#register(Item, BuiltinItemRenderer)}.
 *
 * @deprecated Please use {@link BuiltinItemRendererRegistry.DynamicItemRenderer} instead.
 */
@Deprecated
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface BuiltinItemRenderer {
	/**
	 * Renders an item stack.
	 *
	 * @param stack           the rendered item stack
	 * @param matrices        the matrix stack
	 * @param vertexConsumers the vertex consumer provider
	 * @param light           the color light multiplier at the rendering position
	 * @param overlay         the overlay UV passed to {@link net.minecraft.client.render.VertexConsumer#overlay(int)}
	 */
	void render(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
