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

package net.fabricmc.fabric.test.tool.attribute;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class ToolAttributeTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// Register a custom shovel that has a mining level of 2 (iron) dynamically.
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_shovel"), new TestShovel(new Item.Settings()));
		// Register a block that requires a shovel that is as strong or stronger than an iron one.
		Block block = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_block"),
				new Block(FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.SAND).requiresTool().build(), MaterialColor.STONE)
						.breakByTool(FabricToolTags.SHOVELS, 2)
						.strength(0.6F)
						.sounds(BlockSoundGroup.GRAVEL)));
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_block"), new BlockItem(block, new Item.Settings()));
	}

	private static class TestShovel extends Item implements DynamicAttributeTool {
		private TestShovel(Settings settings) {
			super(settings);
		}

		@Override
		public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (tag.equals(FabricToolTags.SHOVELS)) {
				return 2;
			}

			return 0;
		}

		@Override
		public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (tag.equals(FabricToolTags.SHOVELS)) {
				return 10.0F;
			}

			return 1.0F;
		}
	}
}
