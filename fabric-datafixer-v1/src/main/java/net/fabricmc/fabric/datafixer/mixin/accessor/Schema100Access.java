package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixers.schemas.Schema100;

@Mixin(Schema100.class)
public interface Schema100Access {
    
    @Invoker
    static void callMethod_5195(Schema schema_1, Map map_1, String string) {
    }
    
    @Invoker
    static TypeTemplate callMethod_5196(Schema schema_1) {
        throw new UnsupportedOperationException("Mixin dummy");
    }

}
