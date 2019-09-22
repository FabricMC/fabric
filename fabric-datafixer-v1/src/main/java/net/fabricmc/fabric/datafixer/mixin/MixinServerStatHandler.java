package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.stat.ServerStatHandler;

@Mixin(ServerStatHandler.class)
public class MixinServerStatHandler {
    // Handled in TagHelper.
    
    // Add ModFixers to asString method
}
