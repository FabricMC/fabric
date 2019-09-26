package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema1460;

@Mixin(Schema1460.class)
public interface Schema1460Access {
    
    @Invoker
    static void callMethod_5232(Schema schema_1, Map map_1, String string) {
    }
    
    @Invoker
    static void callMethod_5273(Schema schema_1, Map map_1, String string) {
    }

}
