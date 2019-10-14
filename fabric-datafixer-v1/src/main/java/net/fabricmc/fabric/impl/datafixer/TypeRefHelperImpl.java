package net.fabricmc.fabric.impl.datafixer;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.api.datafixer.v1.TypeReferenceHelper;
import net.fabricmc.fabric.impl.datafixer.mixin.Schema99Accessor;

public class TypeRefHelperImpl implements TypeReferenceHelper {

	public static final TypeRefHelperImpl INSTANCE = new TypeRefHelperImpl();
	
	private TypeRefHelperImpl() {}

	@Override
	public void registerTypeInTile(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema99Accessor.callMethod_5368(schema, typeMap, name); // registerTypeInTile
	}

	@Override
	public void registerTypeWithEquipment(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema99Accessor.callMethod_5339(schema, typeMap, name); // registerTypeWithEquipment
	}

	@Override
	public void registerSimpleType(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		schema.registerSimple(typeMap, name);
	}

	@Override
	public void registerTypeWithItems(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema99Accessor.callMethod_5346(schema, typeMap, name); // registerTypeWithItems
	}

	@Override
	public void registerTypeWithTemplate(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, Supplier<TypeTemplate> typeTemplateSupplier, String name) {
		schema.register(typeMap, name, typeTemplateSupplier);
	}

}
