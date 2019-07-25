package net.fabricmc.fabric.mixin.idremap;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// NOTE: This probably goes into dimension-fixes
@Mixin(DimensionType.class)
public abstract class MixinDimensionRawIndexFix {
    @Inject(at = @At("RETURN"), method = "byRawId", cancellable = true)
    private static void byRawId(final int id, final CallbackInfoReturnable<DimensionType> info) {
        if (info.getReturnValue() == null || info.getReturnValue().getRawId() != id) {
            for (DimensionType dimension : Registry.DIMENSION) {
                if (dimension.getRawId() == id) {
                    info.setReturnValue(dimension);
                    return;
                }
            }
        }
    }
}
