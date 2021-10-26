package net.fabricmc.fabric.api.fluid.v1.tag;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Provides default fabric fluid tags.
 */
public class FabricFluidTags {
	private static final List<Tag<Fluid>> TAGS;
	public static final Tag<Fluid> FABRIC_FLUID;

	private FabricFluidTags() {}

	public static List<Tag<Fluid>> getFluidTags() {
		return TAGS;
	}

	static {
		TAGS = Lists.newArrayList();
		FABRIC_FLUID = TagFactory.FLUID.create(new Identifier("fabric", "fabric_fluid"));
		TAGS.add(FluidTags.WATER);
		TAGS.add(FluidTags.LAVA);
		TAGS.add(FABRIC_FLUID);
	}
}
