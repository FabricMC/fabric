package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.v1.TagRegistration;

public class CommonItemTags {//todo include fabric tools in common tools
	public static final TagKey<Item> PICKAXES = register("pickaxes");
	public static final TagKey<Item> SHOVELS = register("shovels");
	public static final TagKey<Item> HOES = register("hoes");
	public static final TagKey<Item> AXES = register("axes");
	public static final TagKey<Item> SHEARS = register("shears");
	/**
	 * For throwable weapons, like Minecraft tridents
	 */
	public static final TagKey<Item> SPEARS = register("spears");
	public static final TagKey<Item> SWORDS = register("swords");
	public static final TagKey<Item> BOWS = register("bows");
	/**
	 * @deprecated Use {@link CommonItemTags#PICKAXES}
	 */
	@Deprecated
	public static final TagKey<Item> FABRIC_PICKAXES = registerFabric("pickaxes");
	/**
	 * @deprecated Use {@link CommonItemTags#SHOVELS}
	 */
	@Deprecated
	public static final TagKey<Item> FABRIC_SHOVELS = registerFabric("shovels");
	/**
	 * @deprecated Use {@link CommonItemTags#HOES}
	 */
	@Deprecated
	public static final TagKey<Item> FABRIC_HOES = registerFabric("hoes");
	/**
	 * @deprecated Use {@link CommonItemTags#AXES}
	 */
	@Deprecated
	public static final TagKey<Item> FABRIC_AXES = registerFabric("axes");
	@Deprecated
	public static final TagKey<Item> FABRIC_SHEARS = registerFabric("shears");
	/**
	 * @deprecated Use {@link CommonItemTags#SWORDS}
	 */
	@Deprecated
	public static final TagKey<Item> FABRIC_SWORDS = registerFabric("swords");
	public static final TagKey<Item> IRON_INGOTS = register("iron_ingots");
	public static final TagKey<Item> IRON_ORES = register("iron_ores");
	public static final TagKey<Item> GOLD_ORES = register("gold_ores");
	public static final TagKey<Item> GOLD_INGOTS = register("gold_ingots");
	public static final TagKey<Item> REDSTONE_DUSTS = register("redstone_dusts");
	public static final TagKey<Item> REDSTONE_ORES = register("redstone_ores");
	public static final TagKey<Item> COPPER_INGOTS = register("copper_ingots");
	public static final TagKey<Item> COPPER_ORES = register("copper_ores");
	public static final TagKey<Item> ORES = register("ores");
	public static final TagKey<Item> NETHERITE_ORES = register("netherite_ores");
	public static final TagKey<Item> NETHERITE_INGOTS = register("netherite_ingots");
	public static final TagKey<Item> FOODS = register("foods");
	public static final TagKey<Item> POTIONS = register("potions");
	public static final TagKey<Item> BOOKSHELVES = register("bookshelves");

	/**
	 * Items in this tag are marked as cannot be placed into 'sub' or 'item' inventories, such as shulker boxes.
	 * It is up to the inventory implementer to respect this tag's entries.
	 */
	public static final TagKey<Item> ITEM_INVENTORY_EXCLUDED = register("item_inventory_excluded");
	public static final TagKey<Item> WATER_BUCKET = register("bucket/water");
	public static final TagKey<Item> LAVA_BUCKET = register("bucket/lava");
	public static final TagKey<Item> EMPTY_BUCKET = register("bucket/empty");

	private static TagKey<Item> register(String tagID) {
		return TagRegistration.ITEM_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Item> registerFabric(String tagID) {
		return TagRegistration.ITEM_TAG_REGISTRATION.registerFabric(tagID);
	}
}
