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

package net.fabricmc.fabric.dimension;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.DimensionTypeBuilder;
import net.fabricmc.fabric.api.dimension.EntityTeleporter;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

public class DimensionMod implements ModInitializer {

	static DimensionType EXAMPLE_TYPE;

	@Override
	public void onInitialize() {
		EXAMPLE_TYPE = DimensionTypeBuilder
			.create(new Identifier("fabric", "example_dimension"), 3)
			.factory(ExampleDimension::new)
			.build();

		//Right click a diamond to go to the dim
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.DIAMOND_BLOCK) {
				if(world.getDimension().getType() != EXAMPLE_TYPE){
					player.changeDimension(EXAMPLE_TYPE);
				} else {
					//TODO find a way to handle dim travel from modded dims to vanilla dims
//					FabricTeleporter.INSTANCE.changeDimension(player, DimensionType.OVERWORLD, (entity, previousWorld, newWorld) -> {
//						EntityTeleporter.setEntityLocation(entity, new BlockPos(0, 100, 0));
//						world.setBlockState(new BlockPos(0, 99, 0), Blocks.BEDROCK.getDefaultState());
//					});
				}
			}
			if (world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.EMERALD_BLOCK) {
				if(!world.isClient && world.getDimension().getType() != EXAMPLE_TYPE && hand == Hand.MAIN){
					//Spawns an item entity and tries to move to the other dim
					ItemEntity itemEntity = new ItemEntity(world, hitResult.getBlockPos().getX(), hitResult.getBlockPos().getY() + 1, hitResult.getBlockPos().getZ(), new ItemStack(Items.GOLD_INGOT));
					world.spawnEntity(itemEntity);
					itemEntity.changeDimension(EXAMPLE_TYPE);
				}
			}
			return ActionResult.PASS;
		});

	}

	public static class ExampleDimension extends OverworldDimension {

		public ExampleDimension(World world_1, DimensionType dimensionType_1) {
			super(world_1, dimensionType_1);
		}

		@Override
		public DimensionType getType() {
			return EXAMPLE_TYPE;
		}

		@Override
		public BlockPos getForcedSpawnPoint() {
			return new BlockPos(0, 0, 0);
		}
	}
}
