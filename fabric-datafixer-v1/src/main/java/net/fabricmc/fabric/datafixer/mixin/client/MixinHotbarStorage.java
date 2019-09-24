package net.fabricmc.fabric.datafixer.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.nbt.CompoundTag;

@Environment(EnvType.CLIENT)
@Mixin(HotbarStorage.class)
public class MixinHotbarStorage {
    // Only DataFixer reference done exclusively by client. This is implemented because it stores itemstacks within HotbarStorageEntry.
    // Loading is covered by TagHelper
    // Also add Mod DataVersions to save()
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.putInt(Ljava/lang/String;I)V"), method = "save()V", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onSave(CallbackInfo ci, CompoundTag compoundTag_1) {
        FabricDataFixerImpl.INSTANCE.addFixerVersions(compoundTag_1);
    }
}
