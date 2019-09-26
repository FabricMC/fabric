package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixers.schemas.Schema99;

@Mixin(Schema99.class)
public interface Schema99Access {
    
    @Accessor
    static HookFunction getField_5747() { 
        throw new UnsupportedOperationException("Mixin dummy");
    }
    
    @Invoker
    static void callMethod_5368(Schema schema_1, Map map_1, String string_1) {
    }
    
    @Invoker
    static void callMethod_5377(Schema schema_1, Map map_1, String string) {
    }
    
    @Invoker
    static void callMethod_5339(Schema schema_1, Map map_1, String string) {
    }
    
    @Invoker
    static TypeTemplate callMethod_5353(Schema schema_1) {
        throw new UnsupportedOperationException("Mixin dummy");
    }
    
    @Invoker
    static void callMethod_5346(Schema schema_1, Map map_1, String string) { 
    }
}
