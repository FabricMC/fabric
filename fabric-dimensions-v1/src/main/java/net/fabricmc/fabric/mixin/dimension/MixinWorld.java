package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

@Mixin(World.class)
public abstract class MixinWorld {
	/* World.isDay() and World.isNight() enable the day-night cycle as well as some entity behavior
	   (such as bees). In vanilla, these methods are hardcoded to only work in the overworld. This
	   redirector pretends that all dimensions with a visible sky are DimensionType.OVERWORLD, which
	   makes the time checks for modded dimensions work.

	   Dimension.hasVisibleSky() is true for the overworld, false for the nether and the end, and
	   customizable for modded dimensions. It is already used for time checking in other places
	   such as clocks. */
	@Redirect(method = {"isDay", "isNight"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/Dimension;getType()Lnet/minecraft/world/dimension/DimensionType;"))
	private DimensionType replaceCustomDimensionsWithOverworld(Dimension dimension) {
		return dimension.hasVisibleSky() ? DimensionType.OVERWORLD : dimension.getType();
	}
}
