package net.fabricmc.fabric.test.transfer.fluid;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Movement;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class FluidChuteBlockEntity extends BlockEntity implements Tickable {
	private int tickCounter = 0;

	public FluidChuteBlockEntity() {
		super(FluidTransferTest.FLUID_CHUTE_TYPE);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void tick() {
		if (!world.isClient() && tickCounter++ % 20 == 0) {
			Storage<Fluid> top = FluidStorage.SIDED.find(world, pos.offset(Direction.UP), Direction.DOWN);
			Storage<Fluid> bottom = FluidStorage.SIDED.find(world, pos.offset(Direction.DOWN), Direction.UP);

			if (top != null && bottom != null) {
				Movement.move(top, bottom, fluid -> true, FluidConstants.BUCKET);
			}
		}
	}
}
