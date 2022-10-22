package net.fabricmc.fabric.api.itemgroup.v1;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

/**
 * This class contains default ids for Minecraft's own {@link ItemGroup}.
 *
 * @see IdentifiableItemGroup
 */
public final class MinecraftItemGroups {
	public static final Identifier BUILDING_BLOCKS_ID = new Identifier("minecraft:building_blocks");
	public static final Identifier NATURE_ID = new Identifier("minecraft:nature");
	public static final Identifier FUNCTIONAL_ID = new Identifier("minecraft:functional");
	public static final Identifier REDSTONE_ID = new Identifier("minecraft:redstone");
	public static final Identifier HOTBAR_ID = new Identifier("minecraft:hotbar");
	public static final Identifier SEARCH_ID = new Identifier("minecraft:search");
	public static final Identifier TOOLS_ID = new Identifier("minecraft:tools");
	public static final Identifier COMBAT_ID = new Identifier("minecraft:combat");
	public static final Identifier CONSUMABLES_ID = new Identifier("minecraft:consumables");
	public static final Identifier CRAFTING_ID = new Identifier("minecraft:crafting");
	public static final Identifier SPAWN_EGGS_ID = new Identifier("minecraft:spawn_eggs");
	public static final Identifier INVENTORY_ID = new Identifier("minecraft:inventory");

	public static final Map<ItemGroup, Identifier> MAP = new ImmutableMap.Builder<ItemGroup, Identifier>()
			.put(ItemGroups.BUILDING_BLOCKS, MinecraftItemGroups.BUILDING_BLOCKS_ID)
			.put(ItemGroups.NATURE, MinecraftItemGroups.NATURE_ID)
			.put(ItemGroups.FUNCTIONAL, MinecraftItemGroups.FUNCTIONAL_ID)
			.put(ItemGroups.REDSTONE, MinecraftItemGroups.REDSTONE_ID)
			.put(ItemGroups.HOTBAR, MinecraftItemGroups.HOTBAR_ID)
			.put(ItemGroups.SEARCH, MinecraftItemGroups.SEARCH_ID)
			.put(ItemGroups.TOOLS, MinecraftItemGroups.TOOLS_ID)
			.put(ItemGroups.COMBAT, MinecraftItemGroups.COMBAT_ID)
			.put(ItemGroups.CONSUMABLES, MinecraftItemGroups.CONSUMABLES_ID)
			.put(ItemGroups.CRAFTING, MinecraftItemGroups.CRAFTING_ID)
			.put(ItemGroups.SPAWN_EGGS, MinecraftItemGroups.SPAWN_EGGS_ID)
			.put(ItemGroups.INVENTORY, MinecraftItemGroups.INVENTORY_ID)
			.build();

	private MinecraftItemGroups() {
	}
}
