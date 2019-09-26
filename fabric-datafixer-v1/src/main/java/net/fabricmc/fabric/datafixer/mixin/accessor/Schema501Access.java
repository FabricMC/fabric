package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema501;

@Mixin(Schema501.class)
public interface Schema501Access {
    
    @Invoker
    static void callMethod_5290(Schema schema_1, Map map_1, String string) {
    }

}
