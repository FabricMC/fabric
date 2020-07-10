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

package net.fabricmc.fabric.test.renderer_registries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRendererRegistry;
import net.minecraft.item.Items;

public class FabricRendererRegistriesTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemOverlayRendererRegistry.add(Items.NETHERITE_SWORD, (matrixStack, renderer, stack, x, y, countLabel) -> {
			renderer.drawWithShadow(matrixStack, "yo", x, y, 0xFFFFFF);
			return false;
		});
		ItemOverlayRendererRegistry.add(Items.DIAMOND, (matrixStack, renderer, stack, x, y, countLabel) -> {
			renderer.drawWithShadow(matrixStack, "?", x + 17 - renderer.getWidth("?"), y + 9, 0xFFFFFF);
			return true;
		});
	}
}
