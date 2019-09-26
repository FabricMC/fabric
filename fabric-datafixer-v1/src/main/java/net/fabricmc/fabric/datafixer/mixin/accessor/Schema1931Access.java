package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema1931;

@Mixin(Schema1931.class)
public interface Schema1931Access {
    
    @Invoker
    static void callMethod_18247(Schema schema_1, Map map_1, String string) {
    }

}
