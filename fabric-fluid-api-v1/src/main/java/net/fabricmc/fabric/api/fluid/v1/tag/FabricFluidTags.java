package net.fabricmc.fabric.api.fluid.v1.tag;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * Provides default fabric fluid tags.
 */
public class FabricFluidTags {
	public static final Tag<Fluid> FABRIC_FLUID = TagFactory.FLUID.create(new Identifier("fabric", "fabric_fluid"));
}
