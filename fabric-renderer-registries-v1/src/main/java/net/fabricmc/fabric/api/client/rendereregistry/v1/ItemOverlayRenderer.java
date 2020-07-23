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

package net.fabricmc.fabric.api.client.rendereregistry.v1;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * This interface allows you to override Minecraft's GUI item overlay rendering (durability bars, item counts
 * and cooldown overlays).<p>
 *
 * With this interface, you could customize this overlay to your heart's content.<p>
 *
 * Example:
 * <blockquote><pre>
 * ItemOverlayRendererRegistry.set(Items.DIAMOND, (matrixStack, renderer, stack, x, y, countLabel) -> {
 * 	renderer.drawWithShadow(matrixStack, "?", x + 17 - renderer.getWidth("?"), y + 9, 0xFFFFFF);
 * 	return true;
 * });
 * </pre></blockquote>
 */
public interface ItemOverlayRenderer {
	/**
	 * Called before Vanilla's overlay rendering. Note that overlay rendering occurs <em>after</em> the enchanted glint
	 * is rendered.
	 * @return <code>true</code> to cancel Vanilla's overlay rendering.
	 */
	@Environment(EnvType.CLIENT)
	boolean renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel);
}
