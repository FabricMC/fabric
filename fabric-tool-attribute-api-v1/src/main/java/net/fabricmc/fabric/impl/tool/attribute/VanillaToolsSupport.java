package net.fabricmc.fabric.impl.tool.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class VanillaToolsSupport implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("fabric-tool-attribute-api-v1");

	@Override
	public void onInitialize() {
		for (Block block : Registry.BLOCK) {
			registerBlockSupport(Registry.BLOCK.getId(block), block);
		}

		registerSupport(FabricToolTags.PICKAXES, 3, Blocks.OBSIDIAN);
		registerSupport(FabricToolTags.PICKAXES, 2, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE);
		registerSupport(FabricToolTags.PICKAXES, 1, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE);
		registerSupport(FabricToolTags.PICKAXES, 0, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.POWERED_RAIL, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.STONE_BUTTON, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);

		registerSupport(FabricToolTags.AXES, 0, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON);

		registerSupport(FabricToolTags.SHOVELS, 0, Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SNOW, Blocks.SNOW_BLOCK);

		registerSupport(FabricToolTags.SHEARS, 0, Blocks.COBWEB, Blocks.REDSTONE_WIRE, Blocks.TRIPWIRE);
	}

	private void registerBlockSupport(Identifier id, Block block) {
		if (!id.getNamespace().equals("minecraft")) return;
		Material material = block.getDefaultState().getMaterial();

		if (material == Material.STONE || material == Material.METAL || material == Material.ANVIL) {
			registerSupport(FabricToolTags.PICKAXES, 0, block);
		}

		if (material == Material.WOOD || material == Material.PLANT || material == Material.REPLACEABLE_PLANT || material == Material.BAMBOO) {
			registerSupport(FabricToolTags.AXES, 0, block);
		}
	}

	private void registerSupport(Tag<Item> tag, int level, Block... blocks) {
		for (Block block : blocks) {
			ToolManager.Entry entry = ToolManager.entry(block);

			if (entry.getMiningLevel(tag) < 0) {
				entry.putBreakByTool(tag, level);
				LOGGER.debug("Registered vanilla tool level: {} for tag {} at level {}.", Registry.BLOCK.getId(block).toString(), tag.getId().toString(), level);
			}
		}
	}
}
