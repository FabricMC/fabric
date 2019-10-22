package net.fabricmc.fabric.impl.datafixer.mixin;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixers.schemas.Schema100;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema100.class)
public interface Schema100Accessor {
	// registerTypeWithArmorAndToolSlots
	@Invoker
	static void callMethod_5195(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		throw new AssertionError("Mixin dummy");
	}
}
