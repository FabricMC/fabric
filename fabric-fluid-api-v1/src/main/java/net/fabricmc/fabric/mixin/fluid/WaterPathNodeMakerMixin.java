package net.fabricmc.fabric.mixin.fluid;

import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.minecraft.entity.ai.pathing.WaterPathNodeMaker;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WaterPathNodeMaker.class)
public class WaterPathNodeMakerMixin {
	//This mixin adds fabric_fluid to the valid fluids for AI movement.

	@Redirect(method = "getDefaultNodeType", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect1(FluidState state, Tag<Fluid> tag) {
		return state.isIn(FluidTags.WATER) || state.isIn(FabricFluidTags.FABRIC_FLUID);
	}

	@Redirect(method = "getNodeType(III)Lnet/minecraft/entity/ai/pathing/PathNodeType;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect2(FluidState state, Tag<Fluid> tag) {
		return state.isIn(FluidTags.WATER) || state.isIn(FabricFluidTags.FABRIC_FLUID);
	}
}
