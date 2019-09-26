package net.fabricmc.fabric.impl.datafixer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.datafixer.mixin.accessor.Schema704Access;
import net.fabricmc.fabric.datafixer.mixin.accessor.Schema705Access;
import net.fabricmc.fabric.datafixer.mixin.accessor.Schema99Access;
import net.minecraft.datafixers.TypeReferences;

/**
 * This is the Schema that all custom DataFixers use or fixing will fail.
 * <p>TODO:</p>
 * <p>Please note when updating the API when a new fix is added, any new registeredTypes in {@link #registerTypes(Schema, Map, Map)} should be added with a comment above it specifying the Schema Version name.</p>
 */
public class FabricSchema extends Schema {

    public static final BiFunction<Integer, Schema, Schema> FABRIC_TYPEREF_SCHEMA = FabricSchema::new;

    public FabricSchema(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        return new HashMap<>();
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        return new HashMap<>();
    }

    public void registerTypes(final Schema schema_1, final Map<String, Supplier<TypeTemplate>> entityTypes,
            final Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        // Schema 99 Types
        schema_1.registerType(false, TypeReferences.LEVEL, DSL::remainder);

        schema_1.registerType(false, TypeReferences.PLAYER, () -> {
            return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema_1)), "EnderItems",
                    DSL.list(TypeReferences.ITEM_STACK.in(schema_1)));
        });

        schema_1.registerType(false, TypeReferences.CHUNK, () -> {
            return DSL.fields("Level",DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema_1)), "TileEntities",DSL.list(TypeReferences.BLOCK_ENTITY.in(schema_1)), "TileTicks",DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema_1)))));
        });

        schema_1.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.string(), blockEntityTypes);
        });

        schema_1.registerType(true, TypeReferences.ENTITY_TREE, () -> {
            return DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(schema_1),
                    TypeReferences.ENTITY.in(schema_1));
        });

        schema_1.registerType(false, TypeReferences.ENTITY_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(true, TypeReferences.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.string(), entityTypes);
        });

        schema_1.registerType(true, TypeReferences.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id",DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema_1)), "tag",DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema_1), "BlockEntityTag",TypeReferences.BLOCK_ENTITY.in(schema_1), "CanDestroy",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)), "CanPlaceOn",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)))),Schema99Access.getField_5747(), HookFunction.IDENTITY);
        });

        schema_1.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
        schema_1.registerType(false, TypeReferences.BLOCK_NAME, () -> {
            return DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString()));
        });

        schema_1.registerType(false, TypeReferences.ITEM_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.STATS, DSL::remainder);

        schema_1.registerType(false, TypeReferences.SAVED_DATA, () -> {
            return DSL.optionalFields("data",DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema_1)),"Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema_1)), "Teams",DSL.list(TypeReferences.TEAM.in(schema_1))));
        });

        schema_1.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
        schema_1.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
        schema_1.registerType(false, TypeReferences.TEAM, DSL::remainder);
        schema_1.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
        schema_1.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);

        // Schema 100 Types

        schema_1.registerType(false, TypeReferences.STRUCTURE, () -> {
            return DSL.optionalFields("entities",DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema_1))), "blocks",DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema_1))), "palette",DSL.list(TypeReferences.BLOCK_STATE.in(schema_1)));
        });

        schema_1.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);

        // Schema 102 types

        schema_1.registerType(true, TypeReferences.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema_1), "tag",DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema_1), "BlockEntityTag",TypeReferences.BLOCK_ENTITY.in(schema_1), "CanDestroy",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)), "CanPlaceOn",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)))),Schema99Access.getField_5747(), HookFunction.IDENTITY);
        });

        // Schema 106 types

        schema_1.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> {
            return DSL.optionalFields("SpawnPotentials",DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TREE.in(schema_1))), "SpawnData",TypeReferences.ENTITY_TREE.in(schema_1));
        });

        // Schema 136 types

        schema_1.registerType(false, TypeReferences.PLAYER, () -> {
            return DSL.optionalFields("RootVehicle",DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema_1)), "Inventory",DSL.list(TypeReferences.ITEM_STACK.in(schema_1)), "EnderItems",DSL.list(TypeReferences.ITEM_STACK.in(schema_1)));
        });

        schema_1.registerType(true, TypeReferences.ENTITY_TREE, () -> {
            return DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema_1)),TypeReferences.ENTITY.in(schema_1));
        });

        // Schema 704 types

        schema_1.registerType(false, TypeReferences.BLOCK_ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), blockEntityTypes);
        });

        schema_1.registerType(true, TypeReferences.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema_1), "tag",DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema_1), "BlockEntityTag",TypeReferences.BLOCK_ENTITY.in(schema_1), "CanDestroy",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)), "CanPlaceOn",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)))),Schema704Access.getField_5745(), HookFunction.IDENTITY);
        });

        // Schema 705 types

        schema_1.registerType(true, TypeReferences.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), entityTypes);
        });

        schema_1.registerType(true, TypeReferences.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema_1), "tag",DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema_1), "BlockEntityTag",TypeReferences.BLOCK_ENTITY.in(schema_1), "CanDestroy",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)), "CanPlaceOn",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)))),Schema705Access.getField_5746(), HookFunction.IDENTITY);
        });

        // Schema 1022 types

        schema_1.registerType(false, TypeReferences.RECIPE, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.PLAYER, () -> {
            return DSL.optionalFields("RootVehicle",DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema_1)), "Inventory",DSL.list(TypeReferences.ITEM_STACK.in(schema_1)), "EnderItems",DSL.list(TypeReferences.ITEM_STACK.in(schema_1)),DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema_1),"ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema_1), "recipeBook",DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema_1)), "toBeDisplayed",DSL.list(TypeReferences.RECIPE.in(schema_1)))));
        });

        schema_1.registerType(false, TypeReferences.HOTBAR, () -> {
            return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema_1)));
        });

        // Schema 1125 types

        schema_1.registerType(false, TypeReferences.ADVANCEMENTS, () -> {
            return DSL.optionalFields("minecraft:adventure/adventuring_time",DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.BIOME.in(schema_1), DSL.constType(DSL.string()))),"minecraft:adventure/kill_a_mob",DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.string()))),"minecraft:adventure/kill_all_mobs",DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.string()))),"minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.string()))));
        });

        schema_1.registerType(false, TypeReferences.BIOME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.ENTITY_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        // Schema 1451v1 types

        schema_1.registerType(false, TypeReferences.CHUNK, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema_1)),"TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema_1)), "TileTicks",DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema_1))), "Sections",DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema_1))))));
        });

        // Schema 1451v4 types

        schema_1.registerType(false, TypeReferences.BLOCK_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        // Schema 1451v6 types

        Supplier<TypeTemplate> supplier_1 = () -> {
            return DSL.compoundList(TypeReferences.ITEM_NAME.in(schema_1), DSL.constType(DSL.intType()));
        };

        schema_1.registerType(false, TypeReferences.STATS, () -> {
            return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined",DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema_1), DSL.constType(DSL.intType())),"minecraft:crafted", (TypeTemplate) supplier_1.get(), "minecraft:used",(TypeTemplate) supplier_1.get(), "minecraft:broken", (TypeTemplate) supplier_1.get(),"minecraft:picked_up", (TypeTemplate) supplier_1.get(),DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier_1.get(), "minecraft:killed",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.intType())),"minecraft:killed_by",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.intType())),"minecraft:custom",DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
        });

        // Schema 1451v7 types

        schema_1.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
            return DSL.optionalFields("Children",DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema_1), "CB",TypeReferences.BLOCK_STATE.in(schema_1), "CC", TypeReferences.BLOCK_STATE.in(schema_1),"CD", TypeReferences.BLOCK_STATE.in(schema_1))));
        });

        // Schema 1460 types

        schema_1.registerType(false, TypeReferences.LEVEL, DSL::remainder);

        schema_1.registerType(false, TypeReferences.RECIPE, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.PLAYER, () -> {
            return DSL.optionalFields("RootVehicle",DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema_1)), "Inventory",DSL.list(TypeReferences.ITEM_STACK.in(schema_1)), "EnderItems",DSL.list(TypeReferences.ITEM_STACK.in(schema_1)),DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema_1),"ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema_1), "recipeBook",DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema_1)), "toBeDisplayed",DSL.list(TypeReferences.RECIPE.in(schema_1)))));
        });

        schema_1.registerType(false, TypeReferences.CHUNK, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema_1)),"TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema_1)), "TileTicks",DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema_1))), "Sections",DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema_1))))));
        });

        schema_1.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), blockEntityTypes);
        });

        schema_1.registerType(true, TypeReferences.ENTITY_TREE, () -> {
            return DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema_1)),
                    TypeReferences.ENTITY.in(schema_1));
        });

        schema_1.registerType(true, TypeReferences.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), entityTypes);
        });

        schema_1.registerType(true, TypeReferences.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema_1), "tag",DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema_1), "BlockEntityTag",TypeReferences.BLOCK_ENTITY.in(schema_1), "CanDestroy",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)), "CanPlaceOn",DSL.list(TypeReferences.BLOCK_NAME.in(schema_1)))),Schema705Access.getField_5746(), HookFunction.IDENTITY);
        });

        schema_1.registerType(false, TypeReferences.HOTBAR, () -> {
            return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema_1)));
        });

        schema_1.registerType(false, TypeReferences.OPTIONS, DSL::remainder);

        schema_1.registerType(false, TypeReferences.STRUCTURE, () -> {
            return DSL.optionalFields("entities",DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema_1))), "blocks",DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema_1))), "palette",DSL.list(TypeReferences.BLOCK_STATE.in(schema_1)));
        });

        schema_1.registerType(false, TypeReferences.BLOCK_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.ITEM_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);

        Supplier<TypeTemplate> supplier_2 = () -> {
            return DSL.compoundList(TypeReferences.ITEM_NAME.in(schema_1), DSL.constType(DSL.intType()));
        };

        schema_1.registerType(false, TypeReferences.STATS, () -> {
            return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined",DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema_1), DSL.constType(DSL.intType())),"minecraft:crafted", (TypeTemplate) supplier_2.get(), "minecraft:used",(TypeTemplate) supplier_2.get(), "minecraft:broken", (TypeTemplate) supplier_2.get(),"minecraft:picked_up", (TypeTemplate) supplier_2.get(),DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier_2.get(), "minecraft:killed",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.intType())),"minecraft:killed_by",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.intType())),"minecraft:custom",DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
        });

        schema_1.registerType(false, TypeReferences.SAVED_DATA, () -> {
            return DSL.optionalFields("data",DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema_1)),"Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema_1)), "Teams",DSL.list(TypeReferences.TEAM.in(schema_1))));
        });

        schema_1.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
            return DSL.optionalFields("Children",DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema_1), "CB",TypeReferences.BLOCK_STATE.in(schema_1), "CC", TypeReferences.BLOCK_STATE.in(schema_1),"CD", TypeReferences.BLOCK_STATE.in(schema_1))));
        });

        schema_1.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
        schema_1.registerType(false, TypeReferences.TEAM, DSL::remainder);

        schema_1.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> {
            return DSL.optionalFields("SpawnPotentials",DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TREE.in(schema_1))), "SpawnData",TypeReferences.ENTITY_TREE.in(schema_1));
        });

        schema_1.registerType(false, TypeReferences.ADVANCEMENTS, () -> {
            return DSL.optionalFields("minecraft:adventure/adventuring_time",DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.BIOME.in(schema_1), DSL.constType(DSL.string()))),"minecraft:adventure/kill_a_mob",DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.string()))),"minecraft:adventure/kill_all_mobs",DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.string()))),"minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria",DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema_1), DSL.constType(DSL.string()))));
        });

        schema_1.registerType(false, TypeReferences.BIOME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.ENTITY_NAME, () -> {
            return DSL.constType(DSL.namespacedString());
        });

        schema_1.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);

        // Schema 1466 types

        schema_1.registerType(false, TypeReferences.CHUNK, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema_1)),"TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema_1)), "TileTicks",DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema_1))), "Sections",DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema_1)))),"Structures",DSL.optionalFields("Starts", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema_1)))));
        });

        schema_1.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
            return DSL.optionalFields("Children",DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema_1), "CB",TypeReferences.BLOCK_STATE.in(schema_1), "CC", TypeReferences.BLOCK_STATE.in(schema_1),"CD", TypeReferences.BLOCK_STATE.in(schema_1))),"biome", TypeReferences.BIOME.in(schema_1));
        });
    }
}