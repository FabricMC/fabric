package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.datafixers.types.templates.Hook.HookFunction;

import net.minecraft.datafixers.schemas.Schema99;

@Mixin(Schema99.class)
public interface Schema99Access {
    
    @Accessor
    static HookFunction getField_5747() { throw new UnsupportedOperationException(); }
}
