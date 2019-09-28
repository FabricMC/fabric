package net.fabricmc.fabric.api.datafixer.v1;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.impl.datafixer.TypeRefHelperImpl;

/**
 * A helper which deals with registering Entities and BlockEntities along with registering their subTags to be fixed.
 * @author Vince
 *
 */
public interface TypeReferenceHelper {
	public static final TypeReferenceHelper HELPER = TypeRefHelperImpl.INSTANCE;
	
	public void registerEntitySimple(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name);
	
	public void registerEntityWithEquiqmentRef(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name);
	
	public void registerEntityWithArmorSlots(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name);

	public void registerBlockEntitySimple(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap, String name);

	public void registerBlockEntityWithItemRef(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap, String name);
}
