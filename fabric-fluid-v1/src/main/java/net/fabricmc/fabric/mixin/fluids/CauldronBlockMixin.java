package net.fabricmc.fabric.mixin.fluids;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.FluidView;
import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSinks;
import net.fabricmc.fabric.api.fluids.v1.minecraft.blocks.CauldronFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.world.SidedFluidContainer;

@Mixin (CauldronBlock.class)
public class CauldronBlockMixin implements SidedFluidContainer {
	@Override
	public FluidContainer getContainer(World world, BlockPos pos, Direction face) {
		return new CauldronFluidVolume(world, pos);
	}

	@Inject(method = "onUse", at = @At("TAIL"), cancellable = true)
	private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		FluidContainer container = FluidView.getFluidContainer(player.getStackInHand(hand), ItemSinks.playerItemSink(player));
		if(!container.isImmutable()) {
			if (container.isEmpty()) {
				container.consume((FluidVolume) this.getContainer(world, pos, null), Action.PERFORM);
			} else {
				// take buckets out
				for (FluidVolume volume : container) {
					this.getContainer(world, pos, null).consume(volume, Action.PERFORM);
				}
			}
			cir.setReturnValue(ActionResult.SUCCESS);
		}
	}
}
