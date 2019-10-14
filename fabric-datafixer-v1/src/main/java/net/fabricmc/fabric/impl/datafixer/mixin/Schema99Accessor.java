package net.fabricmc.fabric.impl.datafixer.mixin;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixers.schemas.Schema99;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema99.class)
public interface Schema99Accessor {
	/**
	 * registerTypeWithEquipment
	 * @param schema
	 * @param entityMap
	 * @param name
	 */
	@Invoker
	static void callMethod_5339(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name) {
		throw new AssertionError("Mixin Dummy");
	}

	/**
	 * registerTypeWithItems
	 * @param schema
	 * @param typeMap
	 * @param name
	 */
	@Invoker
	static void callMethod_5346(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		throw new AssertionError("Mixin Dummy");
	}

	/**
	 * registerTypeInTile
	 * @param schema
	 * @param typeMap
	 * @param name
	 */
	@Invoker
	static void callMethod_5368(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		throw new AssertionError("Mixin Dummy");
	}
}
