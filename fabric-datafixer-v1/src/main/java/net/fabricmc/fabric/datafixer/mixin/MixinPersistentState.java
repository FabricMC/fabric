package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.PersistentState;

@Mixin(PersistentState.class)
public class MixinPersistentState {
    // Add our mod's data versions to method_17919
    
    // method_17919 makes sense as writeStateToFile(File)
}
