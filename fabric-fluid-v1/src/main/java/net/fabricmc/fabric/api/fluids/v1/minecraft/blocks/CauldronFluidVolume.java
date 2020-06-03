package net.fabricmc.fabric.api.fluids.v1.minecraft.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidUtil;

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
	public Fluid fluid() {
		return this.amount() == 0 ? Fluids.EMPTY : Fluids.WATER;
	}

	@Override
	public CompoundTag data() {
		return new CompoundTag();
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		if (FluidUtil.miscible(fluid, Fluids.WATER)) {
			amount = Drops.floor(Math.min(amount, this.amount()), FRACTION);
			long change = this.addAmount(-amount, action);
			return new SimpleFluidVolume(Fluids.WATER, change);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public long amount() {
		BlockState state = this.world.getBlockState(this.pos);

		if (state.getBlock() instanceof CauldronBlock) {
			return state.get(CauldronBlock.LEVEL) * FRACTION;
		}

		return 0;
	}

	private long addAmount(long amount, Action action) {
		amount = Math.min(amount, Drops.getBucket());
		BlockState state = this.world.getBlockState(this.pos);

		if (state.getBlock() instanceof CauldronBlock) {
			int level = state.get(CauldronBlock.LEVEL);
			int newLevel = (int) (level + (amount / Drops.getBucket()));

			if (newLevel <= 0) {
				if (level != 0 && action.perform()) {
					this.world.setBlockState(this.pos, state.with(CauldronBlock.LEVEL, 0));
				}

				return (-level) * Drops.getBucket(); // decreased to 0
			} else if (newLevel > MAX_LEVEL) {
				newLevel = MAX_LEVEL;
			}

			if (newLevel != level && action.perform()) {
				this.world.setBlockState(this.pos, state.with(CauldronBlock.LEVEL, newLevel));
			}

			return (newLevel - level) * Drops.getBucket(); // decreased by some amount
		}
		return 0;
	}

	@Override
	public FluidVolume add(FluidVolume container, Action action) {
		if (container.fluid().equals(Fluids.WATER)) {
			long amount = Drops.floor(Math.min(container.amount(), Drops.getBucket()), container.amount());
			long change = this.addAmount(amount, action);
			return new SimpleFluidVolume(container.fluid(), amount - change, container.data());
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public boolean isEmpty() {
		return this.amount() == 0;
	}

	@Override
	public boolean isImmutable() {
		return false;
	}
}
