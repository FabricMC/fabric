package net.fabricmc.fabric.test.datafixer.v1;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;

import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Supplier;

public class ChestSchema1 extends IdentifierNormalizingSchema {
	public ChestSchema1(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(map, DataFixerTest.OLD_CHEST_ID.toString(), (() -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))));
		return map;
	}
}
