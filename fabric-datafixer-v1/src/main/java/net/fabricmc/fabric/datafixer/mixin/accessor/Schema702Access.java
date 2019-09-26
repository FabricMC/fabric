package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixers.schemas.Schema702;

@Mixin(Schema702.class)
public interface Schema702Access {
    @Invoker
    static void callMethod_5292(Schema schema_1, Map map_1, String string) {
    }

}
