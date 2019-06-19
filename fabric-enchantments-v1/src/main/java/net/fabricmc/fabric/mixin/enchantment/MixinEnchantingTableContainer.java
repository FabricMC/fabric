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

package net.fabricmc.fabric.mixin.enchantment;

import net.fabricmc.fabric.api.enchantment.EnchantingPowerProvider;
import net.minecraft.block.BlockState;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableContainer.class)
public abstract class MixinEnchantingTableContainer {
	private World fabric_world;
	private BlockPos fabric_blockPos;

	@Inject(method = "method_17411", at = @At("HEAD"))
	private void onEnchantmentCalculation(ItemStack itemStack, World world, BlockPos blockPos, CallbackInfo callbackInfo) {
		fabric_world = world;
		fabric_blockPos = blockPos;
	}

	@ModifyVariable(method = "method_17411", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0), ordinal = 0)
	private int changeEnchantingPower(int power) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for(int zOffset = -1; zOffset <= 1; ++zOffset) {
			for(int xOffset = -1; xOffset <= 1; ++xOffset) {
				if ((zOffset != 0 || xOffset != 0) && fabric_world.isAir(mutable.set(fabric_blockPos).add(xOffset, 0, zOffset)) && fabric_world.isAir(mutable.offset(Direction.UP))) {
					power += fabric_getEnchantingPower(mutable.set(fabric_blockPos).add(xOffset * 2, 0, zOffset * 2));
					power += fabric_getEnchantingPower(mutable.offset(Direction.UP));
					if (xOffset != 0 && zOffset != 0) {
						power += fabric_getEnchantingPower(mutable.set(fabric_blockPos).add(xOffset * 2, 0, zOffset));
						power += fabric_getEnchantingPower(mutable.offset(Direction.UP));
						power += fabric_getEnchantingPower(mutable.set(fabric_blockPos).add(xOffset, 0, zOffset * 2));
						power += fabric_getEnchantingPower(mutable.offset(Direction.UP));
					}
				}
			}
		}
		return power;
	}

	private int fabric_getEnchantingPower(BlockPos blockPos) {
		BlockState blockState = fabric_world.getBlockState(blockPos);
        if(blockState.getBlock() instanceof EnchantingPowerProvider) {
        	return ((EnchantingPowerProvider) blockState.getBlock()).getEnchantingPower(blockState, fabric_world, blockPos);
        }
        return 0;
	}
}
