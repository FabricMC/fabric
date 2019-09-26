package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema808;

@Mixin(Schema808.class)
public interface Schema808Access {
    
    @Invoker
    static void callMethod_5309(Schema schema_1, Map map_1, String string) {
    }

}
