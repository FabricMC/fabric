package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema1800;

@Mixin(Schema1800.class)
public interface Schema1800Access {
    
    @Invoker
    static void callMethod_5285(Schema schema_1, Map map_1, String string) {
    }

}
