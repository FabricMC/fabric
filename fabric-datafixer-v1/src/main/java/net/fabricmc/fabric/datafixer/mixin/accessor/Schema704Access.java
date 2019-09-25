package net.fabricmc.fabric.datafixer.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.datafixers.types.templates.Hook.HookFunction;

import net.minecraft.datafixers.schemas.Schema704;

@Mixin(Schema704.class)
public interface Schema704Access {
    
    @Accessor
    static HookFunction getField_5745() { 
        throw new UnsupportedOperationException("Mixin dummy");
    }
}
