package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.structure.Structure;

@Mixin(Structure.class)
public class MixinStructure {
    // Add ModFixers to toTag
}
