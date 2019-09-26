package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema1904;

@Mixin(Schema1904.class)
public interface Schema1904Access {
    
    @Invoker
    static void callMethod_16050(Schema schema_1, Map map_1, String string) {
    }

}
