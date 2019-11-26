package net.fabricmc.fabric.impl.datafixer.mixin;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.datafixers.schemas.Schema100;

@Mixin(Schema100.class)
public interface Schema100Accessor {
	// registerTypeWithArmorAndToolSlots
	@Invoker("method_5195")
	static void registerTypeWithArmorAndToolSlots(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		throw new AssertionError("Mixin dummy");
	}
}
