package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.structure.Structure;

@Mixin(Structure.class)
public class MixinStructure {
    // Possibly add DataVersions to toTag (at bottom). Debatable if required or not.
}
