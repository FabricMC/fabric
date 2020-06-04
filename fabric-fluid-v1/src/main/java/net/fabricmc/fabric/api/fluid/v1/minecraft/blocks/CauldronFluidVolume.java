package net.fabricmc.fabric.api.fluid.v1.minecraft.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluid.v1.Action;
import net.fabricmc.fabric.api.fluid.v1.FluidView;
import net.fabricmc.fabric.api.fluid.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.math.Drops;

public class CauldronFluidVolume implements FluidVolume {
	public static final int MAX_LEVEL = CauldronBlock.LEVEL.getValues().stream().max(Integer::compareTo).orElse(0);
	public static final long FRACTION = Drops.getBucket() / 3;
	private final World world;
	private final BlockPos pos;

	public CauldronFluidVolume(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		if (FluidView.mixable(fluid, Fluids.WATER)) {
			amount = Drops.floor(Math.min(amount, this.getAmount()), FRACTION);
			long change = this.addAmount(-amount, action);
			return new SimpleFluidVolume(Fluids.WATER, change);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public FluidVolume consume(FluidVolume volume, Action action) {
		if (volume.getFluid().equals(Fluids.WATER)) {
			long amount = Drops.floor(volume.getAmount(), FRACTION);
			amount = this.addAmount(amount, Action.SIMULATE);
			amount = volume.drain(amount, Action.SIMULATE).getAmount();

			if (action.isSimulation()) {
				volume = volume.simpleCopy();
			}

			volume.drain(amount, Action.PERFORM);

			if (action.shouldPerform()) {
				this.addAmount(amount, Action.PERFORM);
			}
		}

		return volume;
	}

	@Override
	public long getAmount() {
		BlockState state = this.world.getBlockState(this.pos);

		if (state.getBlock() instanceof CauldronBlock) {
			return state.get(CauldronBlock.LEVEL) * FRACTION;
		}

		return 0;
	}

	@Override
	public Fluid getFluid() {
		return this.getAmount() == 0 ? Fluids.EMPTY : Fluids.WATER;
	}

	@Override
	public boolean isEmpty() {
		return this.getAmount() == 0;
	}

	@Override
	public CompoundTag getData() {
		return new CompoundTag();
	}

	private long addAmount(long amount, Action action) {
		amount = Math.min(amount, Drops.getBucket());
		BlockState state = this.world.getBlockState(this.pos);

		if (state.getBlock() instanceof CauldronBlock) {
			int level = state.get(CauldronBlock.LEVEL);
			int newLevel = (int) (level + (amount / FRACTION));

			if (newLevel <= 0) {
				if (level != 0 && action.shouldPerform()) {
					this.world.setBlockState(this.pos, state.with(CauldronBlock.LEVEL, 0));
				}

				return (-level) * FRACTION; // decreased to 0
			} else if (newLevel > MAX_LEVEL) {
				newLevel = MAX_LEVEL;
			}

			if (newLevel != level && action.shouldPerform()) {
				this.world.setBlockState(this.pos, state.with(CauldronBlock.LEVEL, newLevel));
			}

			return (newLevel - level) * FRACTION; // decreased by some amount
		}

		return 0;
	}

	@Override
	public boolean isImmutable() {
		return false;
	}
}
