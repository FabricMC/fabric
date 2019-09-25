package net.fabricmc.fabric.datafixer.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.datafixers.types.templates.Hook.HookFunction;

import net.minecraft.datafixers.schemas.Schema705;

@Mixin(Schema705.class)
public interface Schema705Access {
    
    @Accessor
    static HookFunction getField_5746() {
        throw new UnsupportedOperationException("Mixin dummy");
    }
}
