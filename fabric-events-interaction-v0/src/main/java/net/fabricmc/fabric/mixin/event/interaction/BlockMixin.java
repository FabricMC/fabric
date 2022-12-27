package net.fabricmc.fabric.mixin.event.interaction;

import net.fabricmc.fabric.api.event.player.PlayerPlaceBlockEvents;

import net.minecraft.block.Block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
	@Inject(method = "onPlaced", at = @At("HEAD"))
	private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
		PlayerPlaceBlockEvents.AFTER.invoker().afterBlockPlace(world, placer, pos, state);
	}
}
