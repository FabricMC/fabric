package net.fabricmc.fabric.api.fluids.minecraft.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluids.containers.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.containers.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.containers.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.math.Drops;
import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;

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
	public Identifier fluid() {
		return this.amount() == 0 ? FluidIds.EMPTY : FluidIds.WATER;
	}

	@Override
	public CompoundTag data() {
		return new CompoundTag();
	}

	@Override
	public long amount() {
		BlockState state = this.world.getBlockState(this.pos);
		if (state.getBlock() instanceof CauldronBlock) {
			return state.get(CauldronBlock.LEVEL) * FRACTION;
		}
		return 0;
	}

	@Override
	public FluidVolume drain(Identifier fluid, long amount, boolean simulate) {
		if (FluidIds.miscible(fluid, FluidIds.WATER)) {
			amount = Drops.floor(Math.min(amount, this.amount()), FRACTION);
			long change = this.addAmount(-amount, !simulate);
			return new SimpleFluidVolume(FluidIds.WATER, change);
		}
		return ImmutableFluidVolume.EMPTY;
	}

	private long addAmount(long amount, boolean update) {
		amount = Math.min(amount, Drops.getBucket());
		BlockState state = this.world.getBlockState(this.pos);
		if (state.getBlock() instanceof CauldronBlock) {
			int level = state.get(CauldronBlock.LEVEL);
			int newLevel = (int) (level + (amount / Drops.getBucket()));
			if (newLevel <= 0) {
				if (level != 0 && update) {
					this.world.setBlockState(this.pos, state.with(CauldronBlock.LEVEL, 0));
				}
				return (-level) * Drops.getBucket(); // decreased to 0
			} else if (newLevel > MAX_LEVEL) {
				newLevel = MAX_LEVEL;
			}
			if (newLevel != level && update) {
				this.world.setBlockState(this.pos, state.with(CauldronBlock.LEVEL, newLevel));
			}
			return (newLevel - level) * Drops.getBucket(); // decreased by some amount
		}
		return 0;
	}

	@Override
	public FluidVolume add(FluidVolume container, boolean simulate) {
		if (container.fluid().equals(FluidIds.WATER)) {
			long amount = Drops.floor(Math.min(container.amount(), Drops.getBucket()), container.amount());
			long change = this.addAmount(amount, !simulate);
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
