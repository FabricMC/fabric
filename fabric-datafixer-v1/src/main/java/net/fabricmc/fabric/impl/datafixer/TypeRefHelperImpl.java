package net.fabricmc.fabric.impl.datafixer;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.api.datafixer.v1.TypeReferenceHelper;

public class TypeRefHelperImpl implements TypeReferenceHelper {

	public static final TypeRefHelperImpl INSTANCE = new TypeRefHelperImpl();
	
	private TypeRefHelperImpl() {}

	@Override
	public void registerEntitySimple(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerEntityWithEquiqmentRef(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerEntityWithArmorSlots(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerBlockEntitySimple(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerBlockEntityWithItemRef(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap, String name) {
		// TODO Auto-generated method stub
		
	}
	
}
