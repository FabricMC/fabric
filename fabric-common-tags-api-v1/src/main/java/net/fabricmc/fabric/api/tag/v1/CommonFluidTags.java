package net.fabricmc.fabric.api.tag.v1;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.tag.common.TagRegistration;

/**
 * See {@link net.minecraft.tag.FluidTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public class CommonFluidTags {
	public static final TagKey<Fluid> LAVA = register("lava");
	public static final TagKey<Fluid> WATER = register("water");
	public static final TagKey<Fluid> MILK = register("milk");
	public static final TagKey<Fluid> HONEY = register("honey");

	private static TagKey<Fluid> register(String tagID) {
		return TagRegistration.FLUID_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Fluid> registerFabric(String tagID) {
		return TagRegistration.FLUID_TAG_REGISTRATION.registerFabric(tagID);
	}
}
