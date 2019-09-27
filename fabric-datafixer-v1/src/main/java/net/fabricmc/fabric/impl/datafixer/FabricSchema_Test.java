/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.datafixer;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema704Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema705Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema99Access;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.schemas.Schema100;
import net.minecraft.datafixers.schemas.Schema102;
import net.minecraft.datafixers.schemas.Schema1022;
import net.minecraft.datafixers.schemas.Schema106;
import net.minecraft.datafixers.schemas.Schema107;
import net.minecraft.datafixers.schemas.Schema1125;
import net.minecraft.datafixers.schemas.Schema135;
import net.minecraft.datafixers.schemas.Schema143;
import net.minecraft.datafixers.schemas.Schema1451;
import net.minecraft.datafixers.schemas.Schema1451v1;
import net.minecraft.datafixers.schemas.Schema1451v2;
import net.minecraft.datafixers.schemas.Schema1451v3;
import net.minecraft.datafixers.schemas.Schema1451v4;
import net.minecraft.datafixers.schemas.Schema1451v5;
import net.minecraft.datafixers.schemas.Schema1451v6;
import net.minecraft.datafixers.schemas.Schema1451v7;
import net.minecraft.datafixers.schemas.Schema1460;
import net.minecraft.datafixers.schemas.Schema1466;
import net.minecraft.datafixers.schemas.Schema1470;
import net.minecraft.datafixers.schemas.Schema1481;
import net.minecraft.datafixers.schemas.Schema1483;
import net.minecraft.datafixers.schemas.Schema1486;
import net.minecraft.datafixers.schemas.Schema1510;
import net.minecraft.datafixers.schemas.Schema1800;
import net.minecraft.datafixers.schemas.Schema1801;
import net.minecraft.datafixers.schemas.Schema1904;
import net.minecraft.datafixers.schemas.Schema1906;
import net.minecraft.datafixers.schemas.Schema1909;
import net.minecraft.datafixers.schemas.Schema1920;
import net.minecraft.datafixers.schemas.Schema1928;
import net.minecraft.datafixers.schemas.Schema1929;
import net.minecraft.datafixers.schemas.Schema1931;
import net.minecraft.datafixers.schemas.Schema501;
import net.minecraft.datafixers.schemas.Schema700;
import net.minecraft.datafixers.schemas.Schema701;
import net.minecraft.datafixers.schemas.Schema702;
import net.minecraft.datafixers.schemas.Schema703;
import net.minecraft.datafixers.schemas.Schema704;
import net.minecraft.datafixers.schemas.Schema705;
import net.minecraft.datafixers.schemas.Schema808;
import net.minecraft.datafixers.schemas.Schema99;

/**
 * This is the Schema that all custom DataFixers use or fixing will fail because the TypeReferences would have not been registered to the fixer.
 * <p>
 * Please note when updating the API when a new Schema is added, any new registeredTypes in {@link #registerTypes(Schema, Map, Map)} should be added with a comment above it specifying the Schema Version name.
 * </p>
 */
public class FabricSchema_Test extends Schema {
	
	private static final Schema mcSchema = Schemas.getFixer().getSchema(SharedConstants.getGameVersion().getWorldVersion());
	
	public static final BiFunction<Integer, Schema, Schema> FABRIC_TYPEREF_SCHEMA = (version, parent) -> {
		
		Schema schema = new Schema99(version, parent);
		new Schema100(version, schema);
		new Schema102(version, schema);
		new Schema106(version, schema);
		new Schema107(version, schema);
		new Schema135(version, schema);
		new Schema143(version, schema);
		new Schema501(version, schema);
		new Schema700(version, schema);
		new Schema701(version, schema);
		new Schema702(version, schema);
		new Schema703(version, schema);
		new Schema704(version, schema);
		new Schema705(version, schema);
		new Schema808(version, schema);
		new Schema1022(version, schema);
		new Schema1125(version, schema);
		new Schema1451(version, schema);
		new Schema1451v1(version, schema);
		new Schema1451v2(version, schema);
		new Schema1451v3(version, schema);
		new Schema1451v4(version, schema);
		new Schema1451v5(version, schema);
		new Schema1451v6(version, schema);
		new Schema1451v7(version, schema);
		new Schema1460(version, schema);
		new Schema1466(version, schema);
		new Schema1470(version, schema);
		new Schema1481(version, schema);
		new Schema1483(version, schema);
		new Schema1486(version, schema);
		new Schema1510(version, schema);
		new Schema1800(version, schema);
		new Schema1801(version, schema);
		new Schema1904(version, schema);
		new Schema1906(version, schema);
		new Schema1909(version, schema);
		new Schema1920(version, schema);
		new Schema1928(version, schema);
		new Schema1929(version, schema);
		new Schema1931(version, schema);
		
		return schema;
	};

	public FabricSchema_Test(int versionKey, Schema parent) {
		super(inV(versionKey), inS(parent));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

		// LOOP all MC Schema versions from 0 to getWorldVersion?
		
		DataFixer mcFixer = Schemas.getFixer();
		
		for(int x = 0; x<SharedConstants.getGameVersion().getWorldVersion()+1; x++) {
			Schema sch = mcFixer.getSchema(x);
			if(sch==null) {
				continue;
			}
			
			map.putAll(sch.registerEntities(schema));
		}
		
		return map;
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {

		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		
		// LOOP all MC Schema versions from 0 to getWorldVersion?
		
		DataFixer mcFixer = Schemas.getFixer();
		
		for(int x = 0; x<SharedConstants.getGameVersion().getWorldVersion()+1; x++) {
			Schema sch = mcFixer.getSchema(x);
			if(sch==null) {
				continue;
			}
			
			map.putAll(sch.registerBlockEntities(schema));
		}
		
		return map;
	}

	public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> entityTypes, final Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		// Schema 99 Types

		schema.registerType(false, TypeReferences.LEVEL, DSL::remainder);

		schema.registerType(false, TypeReferences.PLAYER, () -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});

		schema.registerType(false, TypeReferences.CHUNK, () -> {
			return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema)))));
		});

		schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
			return DSL.taggedChoiceLazy("id", DSL.string(), blockEntityTypes);
		});

		schema.registerType(true, TypeReferences.ENTITY_TREE, () -> {
			return DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(schema), TypeReferences.ENTITY.in(schema));
		});

		schema.registerType(false, TypeReferences.ENTITY_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(true, TypeReferences.ENTITY, () -> {
			return DSL.taggedChoiceLazy("id", DSL.string(), entityTypes);
		});

		schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
			return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema99Access.getField_5747(), HookFunction.IDENTITY);
		});

		schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
		schema.registerType(false, TypeReferences.BLOCK_NAME, () -> {
			return DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString()));
		});

		schema.registerType(false, TypeReferences.ITEM_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.STATS, DSL::remainder);

		schema.registerType(false, TypeReferences.SAVED_DATA, () -> {
			return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema)), "Teams", DSL.list(TypeReferences.TEAM.in(schema))));
		});

		schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
		schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
		schema.registerType(false, TypeReferences.TEAM, DSL::remainder);
		schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
		schema.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);

		// Schema 100 Types

		schema.registerType(false, TypeReferences.STRUCTURE, () -> {
			return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)));
		});

		schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);

		// Schema 102 types

		schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
			return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema99Access.getField_5747(), HookFunction.IDENTITY);
		});

		// Schema 106 types

		schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> {
			return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TREE.in(schema))), "SpawnData", TypeReferences.ENTITY_TREE.in(schema));
		});

		// Schema 136 types

		schema.registerType(false, TypeReferences.PLAYER, () -> {
			return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});

		schema.registerType(true, TypeReferences.ENTITY_TREE, () -> {
			return DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), TypeReferences.ENTITY.in(schema));
		});

		// Schema 704 types

		schema.registerType(false, TypeReferences.BLOCK_ENTITY, () -> {
			return DSL.taggedChoiceLazy("id", DSL.namespacedString(), blockEntityTypes);
		});

		schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
			return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema704Access.getField_5745(), HookFunction.IDENTITY);
		});

		// Schema 705 types

		schema.registerType(true, TypeReferences.ENTITY, () -> {
			return DSL.taggedChoiceLazy("id", DSL.namespacedString(), entityTypes);
		});

		schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
			return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema705Access.getField_5746(), HookFunction.IDENTITY);
		});

		// Schema 1022 types

		schema.registerType(false, TypeReferences.RECIPE, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.PLAYER, () -> {
			return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema), "ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(schema)))));
		});

		schema.registerType(false, TypeReferences.HOTBAR, () -> {
			return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});

		// Schema 1125 types

		schema.registerType(false, TypeReferences.ADVANCEMENTS, () -> {
			return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))));
		});

		schema.registerType(false, TypeReferences.BIOME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.ENTITY_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		// Schema 1451v1 types

		schema.registerType(false, TypeReferences.CHUNK, () -> {
			return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))))));
		});

		// Schema 1451v4 types

		schema.registerType(false, TypeReferences.BLOCK_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		// Schema 1451v6 types

		Supplier<TypeTemplate> supplier_1 = () -> {
			return DSL.compoundList(TypeReferences.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
		};

		schema.registerType(false, TypeReferences.STATS, () -> {
			return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate) supplier_1.get(), "minecraft:used", (TypeTemplate) supplier_1.get(), "minecraft:broken", (TypeTemplate) supplier_1.get(), "minecraft:picked_up", (TypeTemplate) supplier_1.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier_1.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
		});

		// Schema 1451v7 types

		schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
			return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema), "CB", TypeReferences.BLOCK_STATE.in(schema), "CC", TypeReferences.BLOCK_STATE.in(schema), "CD", TypeReferences.BLOCK_STATE.in(schema))));
		});

		// Schema 1460 types

		schema.registerType(false, TypeReferences.LEVEL, DSL::remainder);

		schema.registerType(false, TypeReferences.RECIPE, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.PLAYER, () -> {
			return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema), "ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(schema)))));
		});

		schema.registerType(false, TypeReferences.CHUNK, () -> {
			return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))))));
		});

		schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
			return DSL.taggedChoiceLazy("id", DSL.namespacedString(), blockEntityTypes);
		});

		schema.registerType(true, TypeReferences.ENTITY_TREE, () -> {
			return DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), TypeReferences.ENTITY.in(schema));
		});

		schema.registerType(true, TypeReferences.ENTITY, () -> {
			return DSL.taggedChoiceLazy("id", DSL.namespacedString(), entityTypes);
		});

		schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
			return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema705Access.getField_5746(), HookFunction.IDENTITY);
		});

		schema.registerType(false, TypeReferences.HOTBAR, () -> {
			return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});

		schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);

		schema.registerType(false, TypeReferences.STRUCTURE, () -> {
			return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)));
		});

		schema.registerType(false, TypeReferences.BLOCK_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.ITEM_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);

		Supplier<TypeTemplate> supplier_2 = () -> {
			return DSL.compoundList(TypeReferences.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
		};

		schema.registerType(false, TypeReferences.STATS, () -> {
			return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate) supplier_2.get(), "minecraft:used", (TypeTemplate) supplier_2.get(), "minecraft:broken", (TypeTemplate) supplier_2.get(), "minecraft:picked_up", (TypeTemplate) supplier_2.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier_2.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
		});

		schema.registerType(false, TypeReferences.SAVED_DATA, () -> {
			return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema)), "Teams", DSL.list(TypeReferences.TEAM.in(schema))));
		});

		schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
			return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema), "CB", TypeReferences.BLOCK_STATE.in(schema), "CC", TypeReferences.BLOCK_STATE.in(schema), "CD", TypeReferences.BLOCK_STATE.in(schema))));
		});

		schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
		schema.registerType(false, TypeReferences.TEAM, DSL::remainder);

		schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> {
			return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TREE.in(schema))), "SpawnData", TypeReferences.ENTITY_TREE.in(schema));
		});

		schema.registerType(false, TypeReferences.ADVANCEMENTS, () -> {
			return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))));
		});

		schema.registerType(false, TypeReferences.BIOME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.ENTITY_NAME, () -> {
			return DSL.constType(DSL.namespacedString());
		});

		schema.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);

		// Schema 1466 types

		schema.registerType(false, TypeReferences.CHUNK, () -> {
			return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)))));
		});

		schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
			return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema), "CB", TypeReferences.BLOCK_STATE.in(schema), "CC", TypeReferences.BLOCK_STATE.in(schema), "CD", TypeReferences.BLOCK_STATE.in(schema))), "biome", TypeReferences.BIOME.in(schema));
		});
	}
	
	private static Schema inS(Schema parent) {
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(parent));
		return parent;
	}

	private static int inV(int versionKey) {
		System.out.println(versionKey);
		return versionKey;
	}
}
