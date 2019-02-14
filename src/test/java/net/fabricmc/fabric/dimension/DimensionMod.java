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
import net.fabricmc.fabric.api.dimension.FabricTeleporter;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
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
			System.out.println("hello");
			if (world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.DIAMOND_BLOCK) {
				if(world.getDimension().getType() != EXAMPLE_TYPE){
					FabricTeleporter.INSTANCE.changeDimension(player, EXAMPLE_TYPE);
				} else {
					FabricTeleporter.INSTANCE.changeDimension(player, DimensionType.OVERWORLD, (entity, previousWorld, newWorld) -> {
						EntityTeleporter.setEntityLocation(entity, new BlockPos(0, 100, 0));
						world.setBlockState(new BlockPos(0, 99, 0), Blocks.BEDROCK.getDefaultState());
					});
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
