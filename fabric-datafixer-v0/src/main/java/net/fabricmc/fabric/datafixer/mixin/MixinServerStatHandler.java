package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.stat.ServerStatHandler;

@Mixin(ServerStatHandler.class)
public class MixinServerStatHandler {
    // Another fix point.
}
