package net.fabricmc.fabric.test.fluid.blocks;


import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.FluidView;
import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSinks;
import net.fabricmc.fabric.api.fluids.v1.world.SidedFluidContainer;

public class TestTankBlock extends Block implements BlockEntityProvider, SidedFluidContainer {
	public TestTankBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new TestTankBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		FluidContainer container = FluidView.getFluidContainer(player.getStackInHand(hand), ItemSinks.playerItemSink(player));
		if (container.isEmpty()) {
			for (FluidVolume volume : this.getContainer(world, pos, null)) {
				container.consume(volume, Action.PERFORM);
			}
		} else {
			// take buckets out
			for (FluidVolume volume : container) {
				this.getContainer(world, pos, null).consume(volume, Action.PERFORM);
			}
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public FluidContainer getContainer(World world, BlockPos pos, Direction face) {
		return ((TestTankBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).getVolume();
	}
}
