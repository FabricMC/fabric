package net.fabricmc.fabric.test.transfer.fluid;

import net.minecraft.fluid.Fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.fluid.Fluids;

public class CreativeFluidStorage implements ExtractionOnlyStorage<Fluid>, StorageView<Fluid> {
	public static final CreativeFluidStorage WATER = new CreativeFluidStorage(Fluids.WATER);
	public static final CreativeFluidStorage LAVA = new CreativeFluidStorage(Fluids.LAVA);

	private final Fluid infiniteFluid;

	private CreativeFluidStorage(Fluid infiniteFluid) {
		this.infiniteFluid = infiniteFluid;
	}

	@Override
	public Fluid resource() {
		return infiniteFluid;
	}

	@Override
	public long amount() {
		return Long.MAX_VALUE;
	}

	@Override
	public long extract(Fluid resource, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(resource, maxAmount);

		if (resource == infiniteFluid) {
			return maxAmount;
		} else {
			return 0;
		}
	}

	@Override
	public boolean forEach(Visitor<Fluid> visitor, Transaction transaction) {
		return visitor.accept(this);
	}

	@Override
	public int getVersion() {
		return 0;
	}
}
