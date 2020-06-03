package net.fabricmc.fabric.test.fluid.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.test.fluid.Register;

public class FluidBlockBlockEntity extends BlockEntity {
	private SimpleFluidVolume volume = new SimpleFluidVolume();

	public FluidBlockBlockEntity() {
		super(Register.BLOCK_ENTITY);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.volume = new SimpleFluidVolume(tag.getCompound("volume"));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("volume", this.volume.toTag(new CompoundTag()));
		return super.toTag(tag);
	}

	public SimpleFluidVolume getVolume() {
		return this.volume;
	}
}
