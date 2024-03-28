package net.fabricmc.fabric.test.datafixer.v1;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

import java.util.Map;
import java.util.function.Supplier;

public class ChestSchema2 extends IdentifierNormalizingSchema {
	public ChestSchema2(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		map.put(DataFixerTest.NEW_CHEST_ID.toString(), map.remove(DataFixerTest.OLD_CHEST_ID.toString()));
		return map;
	}
}
