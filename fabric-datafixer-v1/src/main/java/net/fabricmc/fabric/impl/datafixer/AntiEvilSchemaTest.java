package net.fabricmc.fabric.impl.datafixer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class AntiEvilSchemaTest extends Schema {

	private static final Schema mcSchema = Schemas.getFixer().getSchema(SharedConstants.getGameVersion().getWorldVersion());
	
	public static final BiFunction<Integer, Schema, Schema> ANTI_EVIL_3 = (version, parent) -> {
		
		Schema antiEvil = new SchemaIdentifierNormalize(version, parent);
		
		
		try {
			System.out.println("Start Reflection");
			Field typeTemplates = Schema.class.getDeclaredField("TYPE_TEMPLATES");
			typeTemplates.setAccessible(true);

			Field recursiveTypes = Schema.class.getDeclaredField("RECURSIVE_TYPES");
			recursiveTypes.setAccessible(true);

			// This Schema
			final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES_FABRIC = (Map<String, Supplier<TypeTemplate>>) typeTemplates.get(antiEvil);
			final Object2IntMap<String> RECURSIVE_TYPES_FABRIC = (Object2IntMap<String>) recursiveTypes.get(antiEvil);

			// MC Schema
			final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES_MC = (Map<String, Supplier<TypeTemplate>>) typeTemplates.get(mcSchema);
			final Object2IntMap<String> RECURSIVE_TYPES_MC = (Object2IntMap<String>) recursiveTypes.get(mcSchema);

			TYPE_TEMPLATES_FABRIC.putAll(TYPE_TEMPLATES_MC);
			RECURSIVE_TYPES_FABRIC.putAll(RECURSIVE_TYPES_MC);
			System.out.println("Ended Reflection");
		} catch (ReflectiveOperationException e) {
			CrashReport report = CrashReport.create(e, "Exception while creating DataFixer");

			CrashReportSection section = report.addElement("Schema Version");

			section.add("DataFixer Schema version", () -> Integer.toString(antiEvil.getVersionKey()));

			throw new CrashException(report);
		}
		
		Map<String, Type<?>> selfTypes = null;
		
		Map<String, Type<?>> mcTypes = null;
		
		try {
			Field field = Schema.class.getDeclaredField("TYPES");
			field.setAccessible(true);
			
			selfTypes = (Map<String, Type<?>>) field.get(antiEvil);
			
			mcTypes = (Map<String, Type<?>>) field.get(mcSchema);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		if(selfTypes == null || mcTypes == null) {
			throw new RuntimeException("Reflection failed");
		}
		System.out.println("Reflect");
		selfTypes.putAll(mcTypes);
		System.out.println("Reflected");
		
		return antiEvil;
	};
	
	
	@SuppressWarnings("unchecked")
	public static final BiFunction<Integer, Schema, Schema> ANTI_EVIL_2 = (version, parent) -> {
		Schema schema = new AntiEvilSchemaTest(version, parent);
		System.out.println("Reflect");
		Map<String, Type<?>> selfTypes = null;
		
		Map<String, Type<?>> mcTypes = null;
		
		try {
			Field field = Schema.class.getDeclaredField("TYPES");
			field.setAccessible(true);
			
			selfTypes = (Map<String, Type<?>>) field.get(schema);
			
			mcTypes = (Map<String, Type<?>>) field.get(mcSchema);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		if(selfTypes == null || mcTypes == null) {
			throw new RuntimeException("Reflection failed");
		}
		System.out.println("Reflect");
		selfTypes.putAll(mcTypes);
		System.out.println("Reflected");
		
		System.out.println(new Gson().toJson(selfTypes.keySet()));
		
		return schema;
	};
	 
	
	public static final BiFunction<Integer, Schema, Schema> ANTI_EVIL = AntiEvilSchemaTest::new;
			
			
			/*(version, parent) -> {
		Schema antiEvil = new Schema(version, parent);

		try {
			System.out.println("Start Reflection");
			Field typeTemplates = Schema.class.getDeclaredField("TYPE_TEMPLATES");
			typeTemplates.setAccessible(true);

			Field recursiveTypes = Schema.class.getDeclaredField("RECURSIVE_TYPES");
			recursiveTypes.setAccessible(true);

			// This Schema
			final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES_FABRIC = (Map<String, Supplier<TypeTemplate>>) typeTemplates.get(antiEvil);
			final Object2IntMap<String> RECURSIVE_TYPES_FABRIC = (Object2IntMap<String>) recursiveTypes.get(antiEvil);

			// MC Schema
			final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES_MC = (Map<String, Supplier<TypeTemplate>>) typeTemplates.get(mcSchema);
			final Object2IntMap<String> RECURSIVE_TYPES_MC = (Object2IntMap<String>) recursiveTypes.get(mcSchema);

			TYPE_TEMPLATES_FABRIC.putAll(TYPE_TEMPLATES_MC);
			RECURSIVE_TYPES_FABRIC.putAll(RECURSIVE_TYPES_MC);
			System.out.println("Ended Reflection");
		} catch (ReflectiveOperationException e) {
			CrashReport report = CrashReport.create(e, "Exception while creating DataFixer");

			CrashReportSection section = report.addElement("Schema Version");

			section.add("DataFixer Schema version", () -> Integer.toString(antiEvil.getVersionKey()));

			throw new CrashException(report);
		}
	
		return antiEvil;
	};*/

	private AntiEvilSchemaTest(int versionKey, Schema parent) {
		super(versionKey, parent);
	}
	
	
	
	
	public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		//map.putAll(mcSchema.registerEntities(schema));

		return map;
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		//map.putAll(mcSchema.registerBlockEntities(schema));

		return map;
	}

	public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> entityTypes, final Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		// ReflectionMagic.copyTypeReferences(schema, entityTypes, blockEntityTypes);
		// Why the reflection, hack? You would much prefer this over a 1100+ line class file that needs updating with each MC update.
		/*try {
			System.out.println("Start Reflection");
			Field typeTemplates = Schema.class.getDeclaredField("TYPE_TEMPLATES");
			typeTemplates.setAccessible(true);

			Field recursiveTypes = Schema.class.getDeclaredField("RECURSIVE_TYPES");
			recursiveTypes.setAccessible(true);

			// This Schema
			final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES_FABRIC = (Map<String, Supplier<TypeTemplate>>) typeTemplates.get(schema);
			final Object2IntMap<String> RECURSIVE_TYPES_FABRIC = (Object2IntMap<String>) recursiveTypes.get(schema);

			// MC Schema
			final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES_MC = (Map<String, Supplier<TypeTemplate>>) typeTemplates.get(mcSchema);
			final Object2IntMap<String> RECURSIVE_TYPES_MC = (Object2IntMap<String>) recursiveTypes.get(mcSchema);

			TYPE_TEMPLATES_FABRIC.putAll(TYPE_TEMPLATES_MC);
			RECURSIVE_TYPES_FABRIC.putAll(RECURSIVE_TYPES_MC);
			System.out.println("Ended Reflection");
		} catch (ReflectiveOperationException e) {
			CrashReport report = CrashReport.create(e, "Exception while creating DataFixer");

			CrashReportSection section = report.addElement("Schema Version");

			section.add("DataFixer Schema version", () -> Integer.toString(this.getVersionKey()));

			throw new CrashException(report);
		}*/
		//parent.registerTypes(schema, entityTypes, blockEntityTypes);
		mcSchema.registerTypes(schema, entityTypes, blockEntityTypes);
	}

}
