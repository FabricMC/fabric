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

package net.fabricmc.fabric.api.client.rendereregistry.v1.item;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * This interface allows you to append to Minecraft's GUI item overlay rendering (durability bars, item counts
 * and cooldown overlays).
 */
@FunctionalInterface
public interface PostItemOverlayRenderer {
	/**
	 * No-operation implementation. Used as the default implementation.
	 */
	PostItemOverlayRenderer NO_OP = (matrixStack, renderer, stack, x, y, countLabel) -> { };

	/**
	 * Called after Vanilla's overlay rendering.
	 */
	void renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel);
}
