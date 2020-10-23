package net.fabricmc.fabric.mixin.fluid.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.fabric.impl.fluid.extension.FlowableFluidExtensions;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Property;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin extends Fluid implements FlowableFluidExtensions {
	@Unique
	private int maxLevel = 8;

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}
	
	@Override
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	@Override
	public boolean isFalling(FluidState state) {
		return state.get(FlowableFluid.FALLING);
	}
	
	@ModifyConstant(
		method = {
			"getUpdatedState(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/fluid/FluidState;"
		},
		constant = {@Constant(intValue=8)})
	public int onValue1(int value) {
		return maxLevel;
	}
	
	@ModifyConstant(
		method = {
			"getHeight(Lnet/minecraft/fluid/FluidState;)F",
			"getShape(Lnet/minecraft/fluid/FluidState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"
		},
		constant = {@Constant(floatValue=9.0f)})
	public float onValue2(float value) {
		return maxLevel+1;
	}
	
	@ModifyConstant(
		method = {
			"getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;)Lnet/minecraft/util/math/Vec3d;"
		},
		constant = {@Constant(floatValue=0.8888889f)})
	public float onValue3(float value) {
		return (float)maxLevel/(maxLevel+1);
	}
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/fluid/FluidState.get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"
		),
		method = {
			"getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;)Lnet/minecraft/util/math/Vec3d;",
			"method_15744(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;Lnet/minecraft/block/BlockState;)V"
		})
	public Comparable<?> onGetPropertyValue(FluidState state, Property<?> property) {
		if (property == FlowableFluid.FALLING) {
			return isFalling(state);
		}
		return state.get(property);
	}
}
