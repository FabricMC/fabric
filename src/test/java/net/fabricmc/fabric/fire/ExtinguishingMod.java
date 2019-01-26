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

package net.fabricmc.fabric.fire;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.PunchExtinguishableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.loot.context.LootContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class ExtinguishingMod implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		LOGGER.info("Making a custom block punch-extinguishable");
		Block block = Registry.BLOCK.register(new Identifier("fabric", "punchable"), new NoOutlineBlock(Block.Settings.copy(Blocks.DIRT)));
		Registry.ITEM.register(new Identifier("fabric", "punchable"), new BlockItem(block, new Item.Settings().itemGroup(ItemGroup.MISC)));
		PunchExtinguishableBlockRegistry.INSTANCE.add(block, true);
	}

	private static class NoOutlineBlock extends Block {
		private static final VoxelShape SHAPE = VoxelShapes.empty();

		public NoOutlineBlock(Settings settings) {
			super(settings);
		}

		@Override
		public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, VerticalEntityPosition verticalEntityPosition_1) {
			return SHAPE;
		}
	}
}
