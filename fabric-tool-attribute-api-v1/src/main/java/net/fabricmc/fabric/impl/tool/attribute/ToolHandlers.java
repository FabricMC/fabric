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

package net.fabricmc.fabric.impl.tool.attribute;

import java.util.Arrays;

import net.minecraft.item.Items;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.impl.tool.attribute.handlers.ModdedToolsVanillaBlocksToolHandler;
import net.fabricmc.fabric.impl.tool.attribute.handlers.ModdedToolsModdedBlocksToolHandler;
import net.fabricmc.fabric.impl.tool.attribute.handlers.ShearsVanillaBlocksToolHandler;
import net.fabricmc.fabric.impl.tool.attribute.handlers.VanillaToolsModdedBlocksToolHandler;

/**
 * Entrypoint to register the default tool handlers.
 */
public class ToolHandlers implements ModInitializer {
	@Override
	public void onInitialize() {
		ToolManagerImpl.general().register(new ModdedToolsModdedBlocksToolHandler());
		ToolManagerImpl.general().register(new VanillaToolsModdedBlocksToolHandler());
		ToolManagerImpl.tag(FabricToolTags.PICKAXES).register(new ModdedToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_PICKAXE,
						Items.STONE_PICKAXE,
						Items.IRON_PICKAXE,
						Items.DIAMOND_PICKAXE,
						Items.NETHERITE_PICKAXE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.AXES).register(new ModdedToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_AXE,
						Items.STONE_AXE,
						Items.IRON_AXE,
						Items.DIAMOND_AXE,
						Items.NETHERITE_AXE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SHOVELS).register(new ModdedToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_SHOVEL,
						Items.STONE_SHOVEL,
						Items.IRON_SHOVEL,
						Items.DIAMOND_SHOVEL,
						Items.NETHERITE_SHOVEL
				)
		));
		ToolManagerImpl.tag(FabricToolTags.HOES).register(new ModdedToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_HOE,
						Items.STONE_HOE,
						Items.IRON_HOE,
						Items.DIAMOND_HOE,
						Items.NETHERITE_HOE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SWORDS).register(new ModdedToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_SWORD,
						Items.STONE_SWORD,
						Items.IRON_SWORD,
						Items.DIAMOND_SWORD,
						Items.NETHERITE_SWORD
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SHEARS).register(new ShearsVanillaBlocksToolHandler());
	}
}
