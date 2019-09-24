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
    private static void updateModFixers(DataFixer dataFixer_1, DataFixTypes dataFixTypes_1, CompoundTag compoundTag_1, int dynamicDataVersion, int runtimeDataVersion, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag normal = cir.getReturnValue(); // We do our fixes after vanilla.
        //System.out.println(normal.asString());
        if(normal.containsKey("fabric_test_DataVersion")) {
            System.out.println(true);
        }
        CompoundTag finalTag = FabricDataFixerImpl.INSTANCE.updateWithAllFixers(dataFixer_1, dataFixTypes_1, normal);
        cir.setReturnValue(finalTag);
    }
}
