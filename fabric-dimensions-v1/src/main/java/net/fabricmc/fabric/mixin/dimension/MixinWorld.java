package net.fabricmc.fabric.mixin.dimension;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorld {
	// This redirector un-hardcodes the overworld checks in World.isDay() and World.isNight()
	@Redirect(method = {"isDay", "isNight"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/Dimension;getType()Lnet/minecraft/world/dimension/DimensionType;"))
	private DimensionType replaceCustomDimensionsWithOverworld(Dimension dimension) {
		// Replaces the dimension type with overworld if the dimension has a visible sky.
		// hasVisibleSky() is also used for time checking in other places like clocks, for example.
		return dimension.hasVisibleSky() ? DimensionType.OVERWORLD : dimension.getType();
	}
}
