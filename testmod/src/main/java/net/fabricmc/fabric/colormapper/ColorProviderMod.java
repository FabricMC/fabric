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

package net.fabricmc.fabric.colormapper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockColorMapper;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColorProviderMod implements ClientModInitializer {
	private static final boolean ENABLED = true;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		if (!ENABLED) return;
		LOGGER.info("Initialising ColorProviderMod");

		// Redstone is now the same color as grass
		ColorProviderRegistry.BLOCK.register((block, world, pos, layer) -> {
			BlockColorMapper provider = ColorProviderRegistry.BLOCK.get(Blocks.GRASS);
			return provider == null ? -1 : provider.getColor(block, world, pos, layer);
		}, Blocks.REDSTONE_WIRE);

		// Make white dye glow red.
		ColorProviderRegistry.ITEM.register((item, layer) ->
				(int) (64 * (Math.sin(System.currentTimeMillis() / 5e2) + 3)) << 16,
			Items.WHITE_DYE);
	}
}
