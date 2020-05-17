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

package net.fabricmc.fabric.test.screenhandler.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.test.screenhandler.screen.PositionedBagScreenHandler;

public class PositionedBagItem extends BagItem {
	public PositionedBagItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		user.openHandledScreen(createScreenHandlerFactory(stack, null));
		return TypedActionResult.success(stack);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity user = context.getPlayer();
		ItemStack stack = user.getStackInHand(context.getHand());
		BlockPos pos = context.getBlockPos();
		user.openHandledScreen(createScreenHandlerFactory(stack, pos));
		return ActionResult.SUCCESS;
	}

	private ExtendedScreenHandlerFactory createScreenHandlerFactory(ItemStack stack, BlockPos pos) {
		return new ExtendedScreenHandlerFactory() {
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
				return new PositionedBagScreenHandler(syncId, inventory, new BagInventory(stack), pos);
			}

			@Override
			public Text getDisplayName() {
				return stack.getName();
			}

			@Override
			public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
				buf.writeBoolean(pos != null);
				buf.writeBlockPos(pos != null ? pos : BlockPos.ORIGIN);
			}
		};
	}
}
