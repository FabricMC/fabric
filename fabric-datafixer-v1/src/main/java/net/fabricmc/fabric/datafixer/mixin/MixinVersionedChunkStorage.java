package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.VersionedChunkStorage;

@Mixin(VersionedChunkStorage.class)
public class MixinVersionedChunkStorage {
    // Most input logic in this is handled by the TagHelper already.
    
    
    /**
     * This needs discussion if needed elsewhere
     */
}
