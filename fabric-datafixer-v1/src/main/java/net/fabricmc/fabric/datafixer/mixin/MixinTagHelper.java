package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.DataFixer;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;

@Mixin(TagHelper.class)
public class MixinTagHelper {
    @Inject(at = @At("RETURN"), method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/datafixers/DataFixTypes;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;", cancellable = true)
    private static void updateModFixers(DataFixer vanillaDataFixer$unusued, DataFixTypes dataFixTypes, CompoundTag inputTag$unusued, int dynamicDataVersion$unusued, int runtimeDataVersion$unusued, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag original = cir.getReturnValue(); // We do our fixes after vanilla.
        CompoundTag finalTag = FabricDataFixerImpl.INSTANCE.updateWithAllFixers(dataFixTypes, original);
        cir.setReturnValue(finalTag);
    }
}
