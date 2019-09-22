package net.fabricmc.fabric.datafixer.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.HotbarStorage;

@Environment(EnvType.CLIENT)
@Mixin(HotbarStorage.class)
public class MixinHotbarStorage {
    // Only DataFixer reference done exclusively by client. This is implemented because it stores itemstacks within HotbarStorageEntry.
    // Loading is covered by TagHelper
    // Also add Mod DataVersions to save()
}
