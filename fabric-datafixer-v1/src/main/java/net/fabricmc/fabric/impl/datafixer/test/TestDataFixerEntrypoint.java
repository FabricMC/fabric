package net.fabricmc.fabric.impl.datafixer.test;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;
import net.fabricmc.fabric.api.datafixer.v1.TypeReferenceHelper;
import net.minecraft.datafixers.TypeReferences;

import java.util.Map;
import java.util.function.Supplier;

public class TestDataFixerEntrypoint implements DataFixerEntrypoint {
	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap) {
		TypeReferenceHelper.HELPER.registerSimpleType(schema, entityMap, "TestEntity");
		return entityMap;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap) {
		TypeReferenceHelper.HELPER.registerSimpleType(schema, blockEntityMap, "TestBlockEntity");

		TypeReferenceHelper.HELPER.registerTypeWithTemplate(schema, blockEntityMap, () -> DSL.optionalFields(
			"Left", DSL.list(TypeReferences.ITEM_STACK.in(schema)),
			"Right", DSL.list(TypeReferences.ITEM_STACK.in(schema))), "spookytime:tiny_pumpkin");

		return blockEntityMap;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
	}
}
