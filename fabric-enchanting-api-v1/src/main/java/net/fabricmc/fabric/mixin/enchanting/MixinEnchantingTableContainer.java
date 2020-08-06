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

package net.fabricmc.fabric.mixin.enchanting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.enchanting.v1.EnchantingPowerProvider;

@Mixin(EnchantingTableContainer.class)
public abstract class MixinEnchantingTableContainer {
	@Unique
	private World world;
	@Unique
	private BlockPos blockPos;

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_17411(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At("HEAD"))
	private void onEnchantmentCalculation(ItemStack itemStack, World world, BlockPos blockPos, CallbackInfo callbackInfo) {
		this.world = world;
		this.blockPos = blockPos;
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@ModifyVariable(method = "method_17411(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Ljava/util/Random;setSeed(J)V", shift = At.Shift.BEFORE), ordinal = 0)
	private int changeEnchantingPower(int power) {
		BlockPos.Mutable position = new BlockPos.Mutable();

		for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
			for (int offsetX = -1; offsetX <= 1; ++offsetX) {
				if ((offsetZ != 0 || offsetX != 0) && world.isAir(position.set(blockPos).setOffset(offsetX, 0, offsetZ)) && world.isAir(position.setOffset(Direction.UP))) {
					power += getEnchantingPower(position.set(blockPos).setOffset(offsetX * 2, 0, offsetZ * 2));
					power += getEnchantingPower(position.setOffset(Direction.UP));

					if (offsetX != 0 && offsetZ != 0) {
						power += getEnchantingPower(position.set(blockPos).setOffset(offsetX * 2, 0, offsetZ));
						power += getEnchantingPower(position.setOffset(Direction.UP));
						power += getEnchantingPower(position.set(blockPos).setOffset(offsetX, 0, offsetZ * 2));
						power += getEnchantingPower(position.setOffset(Direction.UP));
					}
				}
			}
		}

		return power;
	}

	@Unique
	private int getEnchantingPower(BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);

		if (blockState.getBlock() instanceof EnchantingPowerProvider) {
			return ((EnchantingPowerProvider) blockState.getBlock()).getEnchantingPower(blockState, world, blockPos);
		}

		return 0;
	}
}
