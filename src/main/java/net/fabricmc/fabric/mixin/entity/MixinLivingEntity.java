package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.block.Climbable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by RedstoneParadox on 1/6/2019.
 */

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow public int stuckArrowTimer;

    @Inject(method = "canClimb", slice = @Slice(from = @At(value = "NEW", target = "block_1")), at = @At(value = "NEW", target = "block_1", ordinal = 0))
    public void canClimb(CallbackInfoReturnable<Boolean> cir) {
        BlockState state = ((LivingEntity) (Object) this).method_16212();
        Block block = state.getBlock();
        if (block instanceof Climbable) {
            cir.setReturnValue(((Climbable) block).canClimb((LivingEntity) (Object) this, state, ((LivingEntity) (Object) this).getPos()));
        }
    }
}
