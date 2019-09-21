package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.VersionedChunkStorage;

@Mixin(VersionedChunkStorage.class)
public class MixinVersionedChunkStorage {
    // Maybe required?
}
