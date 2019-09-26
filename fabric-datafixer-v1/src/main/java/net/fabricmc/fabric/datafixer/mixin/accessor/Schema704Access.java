package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;

import net.minecraft.datafixers.schemas.Schema704;

@Mixin(Schema704.class)
public interface Schema704Access {
    
    @Accessor
    static HookFunction getField_5745() { 
        throw new UnsupportedOperationException("Mixin dummy");
    }
    
    @Invoker
    static void callMethod_5296(Schema schema_1, Map map_1, String string) {
    }
}
