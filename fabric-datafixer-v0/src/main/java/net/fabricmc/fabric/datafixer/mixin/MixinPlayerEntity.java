package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    // Add Mod DataVersions to writeCustomDataToTag
    
}
