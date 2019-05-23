package net.fabricmc.fabric.mixin.enchantment;

import net.fabricmc.fabric.api.enchantment.EnchantingPowerProvider;
import net.minecraft.block.Block;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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

	@ModifyVariable(method = "method_17411", at = @At(value = "INVOKE", target = "Ljava/util/Random;setSeed(J)V", shift = At.Shift.BEFORE), ordinal = 0)
	private int changeEnchantingPower(int power) {
		for(int zOffset = -1; zOffset <= 1; ++zOffset) {
			for(int xOffset = -1; xOffset <= 1; ++xOffset) {
				if ((zOffset != 0 || xOffset != 0) && fabric_world.isAir(fabric_blockPos.add(xOffset, 0, zOffset)) && fabric_world.isAir(fabric_blockPos.add(xOffset, 1, zOffset))) {
					power += fabric_getEnchantingPower(fabric_blockPos.add(xOffset * 2, 0, zOffset * 2));
					power += fabric_getEnchantingPower(fabric_blockPos.add(xOffset * 2, 1, zOffset * 2));
					if (xOffset != 0 && zOffset != 0) {
						power += fabric_getEnchantingPower(fabric_blockPos.add(xOffset * 2, 0, zOffset));
						power += fabric_getEnchantingPower(fabric_blockPos.add(xOffset * 2, 1, zOffset));
						power += fabric_getEnchantingPower(fabric_blockPos.add(xOffset * 2, 1, zOffset * 2));
					}
				}
			}
		}
		return power;
	}

	private int fabric_getEnchantingPower(BlockPos blockPos) {
        Block block = fabric_world.getBlockState(blockPos).getBlock();
        if(block instanceof EnchantingPowerProvider) {
        	return ((EnchantingPowerProvider) block).getEnchantingPower(fabric_world, blockPos);
        }
        return 0;
	}
}
