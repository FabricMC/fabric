package net.fabricmc.fabric.climbable.mixin;

import net.fabricmc.fabric.climbable.api.FabricClimbableTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract BlockState getBlockState();

	@Inject(method = "isClimbing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getBlockState()Lnet/minecraft/block/BlockState;"), cancellable = true)
	public void onIsClimbing(CallbackInfoReturnable<Boolean> info) {
		if (this.getBlockState().matches(FabricClimbableTags.CLIMBABLE)) {
			info.setReturnValue(true);
		}
	}
}
