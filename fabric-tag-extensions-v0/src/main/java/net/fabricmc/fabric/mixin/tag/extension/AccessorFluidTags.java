package net.fabricmc.fabric.mixin.tag.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.RequiredTagList;

@Mixin(FluidTags.class)
public interface AccessorFluidTags {
	@Accessor("REQUIRED_TAGS")
	static RequiredTagList<Fluid> getRequiredTags() {
		throw new UnsupportedOperationException();
	}
}
