package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.data.validate.StructureValidatorProvider;

@Mixin(StructureValidatorProvider.class)
public class MixinStructureValidatorProvider {
    // Check if needed to add Mod DataVersions to method_16880
}
