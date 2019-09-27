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
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema100Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1460Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1470Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1800Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1801Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1904Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1906Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1920Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1928Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema1931Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema501Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema700Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema701Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema702Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema704Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema705Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema808Access;
import net.fabricmc.fabric.impl.datafixer.mixin.accessor.Schema99Access;
import net.minecraft.datafixers.TypeReferences;

/**
 * This is the Schema that all custom DataFixers use or fixing will fail because the TypeReferences would have not been registered to the fixer.
 * <p>
 * Please note when updating the API when a new Schema is added, any new registeredTypes in {@link #registerTypes(Schema, Map, Map)} should be added with a comment above it specifying the Schema Version name.
 * </p>
 * @warning Try to Replace this version dependant class soon with a nice and less long alternative.
 */
public class FabricSchema_Evil extends Schema {

	public static final BiFunction<Integer, Schema, Schema> FABRIC_TYPEREF_SCHEMA = FabricSchema_Evil::new;

	public FabricSchema_Evil(int versionKey, Schema parent) {
		super(inV(versionKey), inS(parent));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

		// Schema 99 Types

		schema.register(map, "Item", (string_1x) -> {
			return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.registerSimple(map, "XPOrb");
		Schema99Access.callMethod_5368(schema, map, "ThrownEgg");
		schema.registerSimple(map, "LeashKnot");
		schema.registerSimple(map, "Painting");
		schema.register(map, "Arrow", (string_1x) -> {
			return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		schema.register(map, "TippedArrow", (string_1x) -> {
			return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		schema.register(map, "SpectralArrow", (string_1x) -> {
			return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema99Access.callMethod_5368(schema, map, "Snowball");
		Schema99Access.callMethod_5368(schema, map, "Fireball");
		Schema99Access.callMethod_5368(schema, map, "SmallFireball");
		Schema99Access.callMethod_5368(schema, map, "ThrownEnderpearl");
		schema.registerSimple(map, "EyeOfEnderSignal");
		schema.register(map, "ThrownPotion", (string_1x) -> {
			return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema), "Potion", TypeReferences.ITEM_STACK.in(schema));
		});
		Schema99Access.callMethod_5368(schema, map, "ThrownExpBottle");
		schema.register(map, "ItemFrame", (string_1x) -> {
			return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
		});
		Schema99Access.callMethod_5368(schema, map, "WitherSkull");
		schema.registerSimple(map, "PrimedTnt");
		schema.register(map, "FallingSand", (string_1x) -> {
			return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema));
		});
		schema.register(map, "FireworksRocketEntity", (string_1x) -> {
			return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.registerSimple(map, "Boat");
		schema.register(map, "Minecart", () -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		Schema99Access.callMethod_5377(schema, map, "MinecartRideable");
		schema.register(map, "MinecartChest", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		Schema99Access.callMethod_5377(schema, map, "MinecartFurnace");
		Schema99Access.callMethod_5377(schema, map, "MinecartTNT");
		schema.register(map, "MinecartSpawner", () -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema));
		});
		schema.register(map, "MinecartHopper", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		Schema99Access.callMethod_5377(schema, map, "MinecartCommandBlock");
		Schema99Access.callMethod_5339(schema, map, "ArmorStand");
		Schema99Access.callMethod_5339(schema, map, "Creeper");
		Schema99Access.callMethod_5339(schema, map, "Skeleton");
		Schema99Access.callMethod_5339(schema, map, "Spider");
		Schema99Access.callMethod_5339(schema, map, "Giant");
		Schema99Access.callMethod_5339(schema, map, "Zombie");
		Schema99Access.callMethod_5339(schema, map, "Slime");
		Schema99Access.callMethod_5339(schema, map, "Ghast");
		Schema99Access.callMethod_5339(schema, map, "PigZombie");
		schema.register(map, "Enderman", (string_1x) -> {
			return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema99Access.callMethod_5353(schema));
		});
		Schema99Access.callMethod_5339(schema, map, "CaveSpider");
		Schema99Access.callMethod_5339(schema, map, "Silverfish");
		Schema99Access.callMethod_5339(schema, map, "Blaze");
		Schema99Access.callMethod_5339(schema, map, "LavaSlime");
		Schema99Access.callMethod_5339(schema, map, "EnderDragon");
		Schema99Access.callMethod_5339(schema, map, "WitherBoss");
		Schema99Access.callMethod_5339(schema, map, "Bat");
		Schema99Access.callMethod_5339(schema, map, "Witch");
		Schema99Access.callMethod_5339(schema, map, "Endermite");
		Schema99Access.callMethod_5339(schema, map, "Guardian");
		Schema99Access.callMethod_5339(schema, map, "Pig");
		Schema99Access.callMethod_5339(schema, map, "Sheep");
		Schema99Access.callMethod_5339(schema, map, "Cow");
		Schema99Access.callMethod_5339(schema, map, "Chicken");
		Schema99Access.callMethod_5339(schema, map, "Squid");
		Schema99Access.callMethod_5339(schema, map, "Wolf");
		Schema99Access.callMethod_5339(schema, map, "MushroomCow");
		Schema99Access.callMethod_5339(schema, map, "SnowMan");
		Schema99Access.callMethod_5339(schema, map, "Ozelot");
		Schema99Access.callMethod_5339(schema, map, "VillagerGolem");
		schema.register(map, "EntityHorse", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema99Access.callMethod_5353(schema));
		});
		Schema99Access.callMethod_5339(schema, map, "Rabbit");
		schema.register(map, "Villager", (string_1x) -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema99Access.callMethod_5353(schema));
		});
		schema.registerSimple(map, "EnderCrystal");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		Schema99Access.callMethod_5339(schema, map, "Shulker");

		// Schema 100 Types

		Schema100Access.callMethod_5195(schema, map, "ArmorStand");
		Schema100Access.callMethod_5195(schema, map, "Creeper");
		Schema100Access.callMethod_5195(schema, map, "Skeleton");
		Schema100Access.callMethod_5195(schema, map, "Spider");
		Schema100Access.callMethod_5195(schema, map, "Giant");
		Schema100Access.callMethod_5195(schema, map, "Zombie");
		Schema100Access.callMethod_5195(schema, map, "Slime");
		Schema100Access.callMethod_5195(schema, map, "Ghast");
		Schema100Access.callMethod_5195(schema, map, "PigZombie");
		schema.register(map, "Enderman", (string_1x) -> {
			return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema100Access.callMethod_5195(schema, map, "CaveSpider");
		Schema100Access.callMethod_5195(schema, map, "Silverfish");
		Schema100Access.callMethod_5195(schema, map, "Blaze");
		Schema100Access.callMethod_5195(schema, map, "LavaSlime");
		Schema100Access.callMethod_5195(schema, map, "EnderDragon");
		Schema100Access.callMethod_5195(schema, map, "WitherBoss");
		Schema100Access.callMethod_5195(schema, map, "Bat");
		Schema100Access.callMethod_5195(schema, map, "Witch");
		Schema100Access.callMethod_5195(schema, map, "Endermite");
		Schema100Access.callMethod_5195(schema, map, "Guardian");
		Schema100Access.callMethod_5195(schema, map, "Pig");
		Schema100Access.callMethod_5195(schema, map, "Sheep");
		Schema100Access.callMethod_5195(schema, map, "Cow");
		Schema100Access.callMethod_5195(schema, map, "Chicken");
		Schema100Access.callMethod_5195(schema, map, "Squid");
		Schema100Access.callMethod_5195(schema, map, "Wolf");
		Schema100Access.callMethod_5195(schema, map, "MushroomCow");
		Schema100Access.callMethod_5195(schema, map, "SnowMan");
		Schema100Access.callMethod_5195(schema, map, "Ozelot");
		Schema100Access.callMethod_5195(schema, map, "VillagerGolem");
		schema.register(map, "EntityHorse", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema100Access.callMethod_5195(schema, map, "Rabbit");
		schema.register(map, "Villager", (string_1x) -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100Access.callMethod_5196(schema));
		});
		Schema100Access.callMethod_5195(schema, map, "Shulker");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");

		// Schema 102, 106 Types -- NONE

		// Schema 107 Types -- REMOVAL

		map.remove("Minecart");

		// Schema 135 Types -- NONE

		// Schema 143 Types -- REMOVAL

		map.remove("TippedArrow");

		// Schema 501 Types

		Schema501Access.callMethod_5290(schema, map, "PolarBear");

		// Schema 700 Types

		Schema700Access.callMethod_5288(schema, map, "ElderGuardian");

		// Schema 701 Types

		Schema701Access.callMethod_5294(schema, map, "WitherSkeleton");
		Schema701Access.callMethod_5294(schema, map, "Stray");

		Schema702Access.callMethod_5292(schema, map, "ZombieVillager");
		Schema702Access.callMethod_5292(schema, map, "Husk");

		// Schema 703 Types

		map.remove("EntityHorse");
		schema.register(map, "Horse", () -> {
			return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.register(map, "Donkey", () -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.register(map, "Mule", () -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.register(map, "ZombieHorse", () -> {
			return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.register(map, "SkeletonHorse", () -> {
			return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});

		// Schema 704 Types -- NONE

		// Schema 705 Types

		schema.registerSimple(map, "minecraft:area_effect_cloud");
		Schema705Access.callMethod_5311(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", (string_1x) -> {
			return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:bat");
		Schema705Access.callMethod_5311(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		Schema705Access.callMethod_5311(schema, map, "minecraft:cave_spider");
		schema.register(map, "minecraft:chest_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:chicken");
		schema.register(map, "minecraft:commandblock_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:cow");
		Schema705Access.callMethod_5311(schema, map, "minecraft:creeper");
		schema.register(map, "minecraft:donkey", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.registerSimple(map, "minecraft:dragon_fireball");
		Schema705Access.callMethod_5330(schema, map, "minecraft:egg");
		Schema705Access.callMethod_5311(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		Schema705Access.callMethod_5311(schema, map, "minecraft:ender_dragon");
		schema.register(map, "minecraft:enderman", (string_1x) -> {
			return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:endermite");
		Schema705Access.callMethod_5330(schema, map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(map, "minecraft:falling_block", (string_1x) -> {
			return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema));
		});
		Schema705Access.callMethod_5330(schema, map, "minecraft:fireball");
		schema.register(map, "minecraft:fireworks_rocket", (string_1x) -> {
			return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.register(map, "minecraft:furnace_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:ghast");
		Schema705Access.callMethod_5311(schema, map, "minecraft:giant");
		Schema705Access.callMethod_5311(schema, map, "minecraft:guardian");
		schema.register(map, "minecraft:hopper_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		schema.register(map, "minecraft:horse", (string_1x) -> {
			return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:husk");
		schema.register(map, "minecraft:item", (string_1x) -> {
			return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.register(map, "minecraft:item_frame", (string_1x) -> {
			return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.registerSimple(map, "minecraft:leash_knot");
		Schema705Access.callMethod_5311(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:mooshroom");
		schema.register(map, "minecraft:mule", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		Schema705Access.callMethod_5311(schema, map, "minecraft:pig");
		Schema705Access.callMethod_5311(schema, map, "minecraft:polar_bear");
		schema.register(map, "minecraft:potion", (string_1x) -> {
			return DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema), "inTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:rabbit");
		Schema705Access.callMethod_5311(schema, map, "minecraft:sheep");
		Schema705Access.callMethod_5311(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		Schema705Access.callMethod_5311(schema, map, "minecraft:silverfish");
		Schema705Access.callMethod_5311(schema, map, "minecraft:skeleton");
		schema.register(map, "minecraft:skeleton_horse", (string_1x) -> {
			return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:slime");
		Schema705Access.callMethod_5330(schema, map, "minecraft:small_fireball");
		Schema705Access.callMethod_5330(schema, map, "minecraft:snowball");
		Schema705Access.callMethod_5311(schema, map, "minecraft:snowman");
		schema.register(map, "minecraft:spawner_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema));
		});
		schema.register(map, "minecraft:spectral_arrow", (string_1x) -> {
			return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:spider");
		Schema705Access.callMethod_5311(schema, map, "minecraft:squid");
		Schema705Access.callMethod_5311(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(map, "minecraft:tnt_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema));
		});
		schema.register(map, "minecraft:villager", (string_1x) -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100Access.callMethod_5196(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:villager_golem");
		Schema705Access.callMethod_5311(schema, map, "minecraft:witch");
		Schema705Access.callMethod_5311(schema, map, "minecraft:wither");
		Schema705Access.callMethod_5311(schema, map, "minecraft:wither_skeleton");
		Schema705Access.callMethod_5330(schema, map, "minecraft:wither_skull");
		Schema705Access.callMethod_5311(schema, map, "minecraft:wolf");
		Schema705Access.callMethod_5330(schema, map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		Schema705Access.callMethod_5311(schema, map, "minecraft:zombie");
		schema.register(map, "minecraft:zombie_horse", (string_1x) -> {
			return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema705Access.callMethod_5311(schema, map, "minecraft:zombie_pigman");
		Schema705Access.callMethod_5311(schema, map, "minecraft:zombie_villager");
		schema.registerSimple(map, "minecraft:evocation_fangs");
		Schema705Access.callMethod_5311(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:illusion_illager");
		schema.register(map, "minecraft:llama", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), "DecorItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.registerSimple(map, "minecraft:llama_spit");
		Schema705Access.callMethod_5311(schema, map, "minecraft:vex");
		Schema705Access.callMethod_5311(schema, map, "minecraft:vindication_illager");

		// Schema 808, 1022, 1451, 1451v1, 1451v2 Types -- NONE

		// Schema 1451v3 Types

		schema.registerSimple(map, "minecraft:egg");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(map, "minecraft:potion", (string_1x) -> {
			return DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		schema.registerSimple(map, "minecraft:wither_skull");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.register(map, "minecraft:arrow", () -> {
			return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema));
		});
		schema.register(map, "minecraft:enderman", () -> {
			return DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.register(map, "minecraft:falling_block", () -> {
			return DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema));
		});
		schema.register(map, "minecraft:spectral_arrow", () -> {
			return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema));
		});
		schema.register(map, "minecraft:chest_minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		schema.register(map, "minecraft:commandblock_minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		schema.register(map, "minecraft:furnace_minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		schema.register(map, "minecraft:hopper_minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		schema.register(map, "minecraft:minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		schema.register(map, "minecraft:spawner_minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema));
		});
		schema.register(map, "minecraft:tnt_minecart", () -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});

		// Schema 1451v4, 1451v5, 1451v6, 1451v7 Types -- NONE

		// Schema 1460 Types

		schema.registerSimple(map, "minecraft:area_effect_cloud");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", (string_1x) -> {
			return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:bat");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:cave_spider");
		schema.register(map, "minecraft:chest_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:chicken");
		schema.register(map, "minecraft:commandblock_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:cow");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:creeper");
		schema.register(map, "minecraft:donkey", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.registerSimple(map, "minecraft:dragon_fireball");
		schema.registerSimple(map, "minecraft:egg");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:ender_dragon");
		schema.register(map, "minecraft:enderman", (string_1x) -> {
			return DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:endermite");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:evocation_fangs");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(map, "minecraft:falling_block", (string_1x) -> {
			return DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema));
		});
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(map, "minecraft:fireworks_rocket", (string_1x) -> {
			return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.register(map, "minecraft:furnace_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:ghast");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:giant");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:guardian");
		schema.register(map, "minecraft:hopper_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});
		schema.register(map, "minecraft:horse", (string_1x) -> {
			return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:husk");
		schema.registerSimple(map, "minecraft:illusion_illager");
		schema.register(map, "minecraft:item", (string_1x) -> {
			return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.register(map, "minecraft:item_frame", (string_1x) -> {
			return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.registerSimple(map, "minecraft:leash_knot");
		schema.register(map, "minecraft:llama", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), "DecorItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		schema.registerSimple(map, "minecraft:llama_spit");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:mooshroom");
		schema.register(map, "minecraft:mule", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:pig");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:polar_bear");
		schema.register(map, "minecraft:potion", (string_1x) -> {
			return DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:rabbit");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:sheep");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:silverfish");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:skeleton");
		schema.register(map, "minecraft:skeleton_horse", (string_1x) -> {
			return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:slime");
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:snowman");
		schema.register(map, "minecraft:spawner_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema));
		});
		schema.register(map, "minecraft:spectral_arrow", (string_1x) -> {
			return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:spider");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:squid");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(map, "minecraft:tnt_minecart", (string_1x) -> {
			return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:vex");
		schema.register(map, "minecraft:villager", (string_1x) -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100Access.callMethod_5196(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:villager_golem");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:vindication_illager");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:witch");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:wither");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:wither_skeleton");
		schema.registerSimple(map, "minecraft:wither_skull");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:wolf");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:zombie");
		schema.register(map, "minecraft:zombie_horse", (string_1x) -> {
			return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});
		Schema1460Access.callMethod_5232(schema, map, "minecraft:zombie_pigman");
		Schema1460Access.callMethod_5232(schema, map, "minecraft:zombie_villager");

		// Schema 1466 -- NONE

		// Schema 1470 Types

		Schema1470Access.callMethod_5280(schema, map, "minecraft:turtle");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:cod_mob");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:tropical_fish");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:salmon_mob");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:puffer_fish");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:phantom");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:dolphin");
		Schema1470Access.callMethod_5280(schema, map, "minecraft:drowned");
		schema.register(map, "minecraft:trident", (string_1x) -> {
			return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema));
		});

		// Schema 1481 -- NONE

		// Schema 1483 Types

		map.put("minecraft:pufferfish", map.remove("minecraft:puffer_fish"));

		// Schema 1486 Types

		map.put("minecraft:cod", map.remove("minecraft:cod_mob"));
		map.put("minecraft:salmon", map.remove("minecraft:salmon_mob"));

		// Schema 1510 Types -- REPLACE

		map.put("minecraft:command_block_minecart", map.remove("minecraft:commandblock_minecart"));
		map.put("minecraft:end_crystal", map.remove("minecraft:ender_crystal"));
		map.put("minecraft:snow_golem", map.remove("minecraft:snowman"));
		map.put("minecraft:evoker", map.remove("minecraft:evocation_illager"));
		map.put("minecraft:evoker_fangs", map.remove("minecraft:evocation_fangs"));
		map.put("minecraft:illusioner", map.remove("minecraft:illusion_illager"));
		map.put("minecraft:vindicator", map.remove("minecraft:vindication_illager"));
		map.put("minecraft:iron_golem", map.remove("minecraft:villager_golem"));
		map.put("minecraft:experience_orb", map.remove("minecraft:xp_orb"));
		map.put("minecraft:experience_bottle", map.remove("minecraft:xp_bottle"));
		map.put("minecraft:eye_of_ender", map.remove("minecraft:eye_of_ender_signal"));
		map.put("minecraft:firework_rocket", map.remove("minecraft:fireworks_rocket"));

		// Schema 1800 -- Types

		Schema1800Access.callMethod_5285(schema, map, "minecraft:panda");
		schema.register(map, "minecraft:pillager", (string_1x) -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), Schema100Access.callMethod_5196(schema));
		});

		// Schema 1801 -- Types

		Schema1801Access.callMethod_5283(schema, map, "minecraft:illager_beast");

		// Schema 1904 -- Type

		Schema1904Access.callMethod_16050(schema, map, "minecraft:cat");

		// Schema 1928 -- REPLACE

		map.remove("minecraft:illager_beast");
		Schema1928Access.callMethod_17998(schema, map, "minecraft:ravager");

		// Schema 1929

		schema.register(map, "minecraft:wandering_trader", (string_1x) -> {
			return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100Access.callMethod_5196(schema));
		});
		schema.register(map, "minecraft:trader_llama", (string_1x) -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), "DecorItem", TypeReferences.ITEM_STACK.in(schema), Schema100Access.callMethod_5196(schema));
		});

		// Schema 1931

		Schema1931Access.callMethod_18247(schema, map, "minecraft:fox");

		return map;
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {

		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

		// Schema 99 Types

		Schema99Access.callMethod_5346(schema, map, "Furnace");
		Schema99Access.callMethod_5346(schema, map, "Chest");
		schema.registerSimple(map, "EnderChest");
		schema.register(map, "RecordPlayer", (string_1x) -> {
			return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema));
		});
		Schema99Access.callMethod_5346(schema, map, "Trap");
		Schema99Access.callMethod_5346(schema, map, "Dropper");
		schema.registerSimple(map, "Sign");
		schema.register(map, "MobSpawner", (string_1x) -> {
			return TypeReferences.UNTAGGED_SPAWNER.in(schema);
		});
		schema.registerSimple(map, "Music");
		schema.registerSimple(map, "Piston");
		Schema99Access.callMethod_5346(schema, map, "Cauldron");
		schema.registerSimple(map, "EnchantTable");
		schema.registerSimple(map, "Airportal");
		schema.registerSimple(map, "Control");
		schema.registerSimple(map, "Beacon");
		schema.registerSimple(map, "Skull");
		schema.registerSimple(map, "DLDetector");
		Schema99Access.callMethod_5346(schema, map, "Hopper");
		schema.registerSimple(map, "Comparator");
		schema.register(map, "FlowerPot", (string_1x) -> {
			return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)));
		});
		schema.registerSimple(map, "Banner");
		schema.registerSimple(map, "Structure");
		schema.registerSimple(map, "EndGateway");

		// Schema 100, 102, 106, 107, 135, 143, 501, 700, 701, 702, 703 Types -- NONE

		// Schema 704 Types

		Schema704Access.callMethod_5296(schema, map, "minecraft:furnace");
		Schema704Access.callMethod_5296(schema, map, "minecraft:chest");
		schema.registerSimple(map, "minecraft:ender_chest");
		schema.register(map, "minecraft:jukebox", (string_1x) -> {
			return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema));
		});
		Schema704Access.callMethod_5296(schema, map, "minecraft:dispenser");
		Schema704Access.callMethod_5296(schema, map, "minecraft:dropper");
		schema.registerSimple(map, "minecraft:sign");
		schema.register(map, "minecraft:mob_spawner", (string_1x) -> {
			return TypeReferences.UNTAGGED_SPAWNER.in(schema);
		});
		schema.registerSimple(map, "minecraft:noteblock");
		schema.registerSimple(map, "minecraft:piston");
		Schema704Access.callMethod_5296(schema, map, "minecraft:brewing_stand");
		schema.registerSimple(map, "minecraft:enchanting_table");
		schema.registerSimple(map, "minecraft:end_portal");
		schema.registerSimple(map, "minecraft:beacon");
		schema.registerSimple(map, "minecraft:skull");
		schema.registerSimple(map, "minecraft:daylight_detector");
		Schema704Access.callMethod_5296(schema, map, "minecraft:hopper");
		schema.registerSimple(map, "minecraft:comparator");
		schema.register(map, "minecraft:flower_pot", (string_1x) -> {
			return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)));
		});
		schema.registerSimple(map, "minecraft:banner");
		schema.registerSimple(map, "minecraft:structure_block");
		schema.registerSimple(map, "minecraft:end_gateway");
		schema.registerSimple(map, "minecraft:command_block");

		// Schema 705 Types -- NONE

		// Schema 808 Types

		Schema808Access.callMethod_5309(schema, map, "minecraft:shulker_box");

		// Schema 1022 Types -- NONE

		// Schema 1125 Types

		schema.registerSimple(map, "minecraft:bed");

		// Schema 1451 Types

		schema.register(map, "minecraft:trapped_chest", () -> {
			return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		});

		// Schema 1451v1 Types -- NONE

		// Schema 1451v2 Types

		schema.register(map, "minecraft:piston", (string_1x) -> {
			return DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(schema));
		});

		// Schema 1451v3, 1451v4 Types -- NONE

		// Schema 1451v5 Types -- REMOVE

		map.remove("minecraft:flower_pot");
		map.remove("minecraft:noteblock");

		// Schema 1451v6, 1451v7 Types -- NONE

		// schema 1460 Types

		Schema1460Access.callMethod_5273(schema, map, "minecraft:furnace");
		Schema1460Access.callMethod_5273(schema, map, "minecraft:chest");
		Schema1460Access.callMethod_5273(schema, map, "minecraft:trapped_chest");
		schema.registerSimple(map, "minecraft:ender_chest");
		schema.register(map, "minecraft:jukebox", (string_1x) -> {
			return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema));
		});
		Schema1460Access.callMethod_5273(schema, map, "minecraft:dispenser");
		Schema1460Access.callMethod_5273(schema, map, "minecraft:dropper");
		schema.registerSimple(map, "minecraft:sign");
		schema.register(map, "minecraft:mob_spawner", (string_1x) -> {
			return TypeReferences.UNTAGGED_SPAWNER.in(schema);
		});
		schema.register(map, "minecraft:piston", (string_1x) -> {
			return DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(schema));
		});
		Schema1460Access.callMethod_5273(schema, map, "minecraft:brewing_stand");
		schema.registerSimple(map, "minecraft:enchanting_table");
		schema.registerSimple(map, "minecraft:end_portal");
		schema.registerSimple(map, "minecraft:beacon");
		schema.registerSimple(map, "minecraft:skull");
		schema.registerSimple(map, "minecraft:daylight_detector");
		Schema1460Access.callMethod_5273(schema, map, "minecraft:hopper");
		schema.registerSimple(map, "minecraft:comparator");
		schema.registerSimple(map, "minecraft:banner");
		schema.registerSimple(map, "minecraft:structure_block");
		schema.registerSimple(map, "minecraft:end_gateway");
		schema.registerSimple(map, "minecraft:command_block");
		Schema1460Access.callMethod_5273(schema, map, "minecraft:shulker_box");
		schema.registerSimple(map, "minecraft:bed");

		// Schema 1466 Types
		map.put("DUMMY", DSL::remainder); // TODO wat

		// Schema 1470 -- NONE

		// Schema 1481 Types

		schema.registerSimple(map, "minecraft:conduit");

		// Schema 1483, 1486, 1510, 1800 -- NONE

		// Schema 1906

		Schema1906Access.callMethod_16052(schema, map, "minecraft:barrel");
		Schema1906Access.callMethod_16052(schema, map, "minecraft:smoker");
		Schema1906Access.callMethod_16052(schema, map, "minecraft:blast_furnace");
		schema.register(map, "minecraft:lectern", (string_1x) -> {
			return DSL.optionalFields("Book", TypeReferences.ITEM_STACK.in(schema));
		});
		schema.registerSimple(map, "minecraft:bell");

		// Schema 1909

		schema.registerSimple(map, "minecraft:jigsaw");

		// Schema 1920
		Schema1920Access.callMethod_17343(schema, map, "minecraft:campfire");

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
