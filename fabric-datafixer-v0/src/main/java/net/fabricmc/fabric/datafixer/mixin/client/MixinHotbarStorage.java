package net.fabricmc.fabric.datafixer.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.HotbarStorage;

@Environment(EnvType.CLIENT)
@Mixin(HotbarStorage.class)
public class MixinHotbarStorage {
    // Only DataFixer refrence done exclusively by client.
    // Covered by TagHelper
    // Also add Mod DataVersions to save()
}
