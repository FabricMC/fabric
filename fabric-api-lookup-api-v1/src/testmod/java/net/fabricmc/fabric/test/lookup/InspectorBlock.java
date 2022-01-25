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

package net.fabricmc.fabric.test.lookup;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.test.lookup.api.Inspectable;
import net.fabricmc.fabric.test.lookup.entity.FabricEntityApiLookupTest;
import net.fabricmc.fabric.test.lookup.item.FabricItemApiLookupTest;

public class InspectorBlock extends Block {
	public InspectorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		Inspectable inspectable = FabricItemApiLookupTest.INSPECTABLE.find(stack, null);

		if (inspectable != null) {
			if (!world.isClient()) {
				player.sendMessage(inspectable.inspect(), true);
			}

			return ActionResult.success(world.isClient());
		}

		return ActionResult.PASS;
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient()) {
			Inspectable inspectable = FabricEntityApiLookupTest.INSPECTABLE.find(entity, null);

			if (inspectable != null) {
				for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
					player.sendMessage(inspectable.inspect(), true);
				}
			}
		}
	}
}
