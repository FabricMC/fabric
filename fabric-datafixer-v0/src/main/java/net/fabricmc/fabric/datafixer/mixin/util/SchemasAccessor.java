package net.fabricmc.fabric.datafixer.mixin.util;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.Schemas;

@Mixin(Schemas.class)
public class SchemasAccessor {
    // TODO make this an actual Accessor Mixin
    @SuppressWarnings("rawtypes")
    @Shadow
    static BiFunction empty;
    

    @SuppressWarnings("unchecked")
    public static BiFunction<Integer,Schema,Schema> getEmpty() {
        return empty;
    }
    
    
    @SuppressWarnings("rawtypes")
    @Shadow
    static BiFunction identNormalize;
    
    @SuppressWarnings("unchecked")
    public static BiFunction<Integer,Schema,Schema> getIdentNormalize() {
        return identNormalize;
    }
}
