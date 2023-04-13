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

package net.fabricmc.fabric.test.event.interaction;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class FakePlayerTests {
	/**
	 * Try placing a sign with a fake player.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testFakePlayerPlaceSign(TestContext context) {
		// This is for Fabric internal testing only, if you copy this to your mod you're on your own...

		BlockPos basePos = new BlockPos(0, 1, 0);
		BlockPos signPos = basePos.up();

		context.setBlockState(basePos, Blocks.STONE.getDefaultState());

		PlayerEntity fakePlayer = FakePlayer.get(context.getWorld());

		BlockPos fakePlayerPos = context.getAbsolutePos(signPos.add(2, 0, 2));
		fakePlayer.setPosition(fakePlayerPos.getX(), fakePlayerPos.getY(), fakePlayerPos.getZ());
		ItemStack signStack = Items.OAK_SIGN.getDefaultStack();
		fakePlayer.setStackInHand(Hand.MAIN_HAND, signStack);

		Vec3d hitPos = context.getAbsolutePos(basePos).toCenterPos().add(0, 0.5, 0);
		BlockHitResult hitResult = new BlockHitResult(hitPos, Direction.UP, context.getAbsolutePos(basePos), false);
		signStack.useOnBlock(new ItemUsageContext(fakePlayer, Hand.MAIN_HAND, hitResult));

		context.checkBlockState(signPos, x -> x.isOf(Blocks.OAK_SIGN), () -> "Sign was not placed");
		context.assertTrue(signStack.isEmpty(), "Sign stack was not emptied");
		context.complete();
	}
}
