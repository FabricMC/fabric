package net.fabricmc.fabric.impl.v1.datagen.generators;

import net.minecraft.item.Items;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tags.v1.CommonItemTags;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
	public ItemTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		generateToolTags();
		generateExcludedItemTag();
	}

	private void generateExcludedItemTag() {
		getOrCreateTagBuilder(CommonItemTags.ITEM_INVENTORY_EXCLUDED)
				.add(Items.SHULKER_BOX)
				.add(Items.BLUE_SHULKER_BOX)
				.add(Items.BROWN_SHULKER_BOX)
				.add(Items.CYAN_SHULKER_BOX)
				.add(Items.GRAY_SHULKER_BOX)
				.add(Items.GREEN_SHULKER_BOX)
				.add(Items.LIGHT_BLUE_SHULKER_BOX)
				.add(Items.LIGHT_GRAY_SHULKER_BOX)
				.add(Items.LIME_SHULKER_BOX)
				.add(Items.MAGENTA_SHULKER_BOX)
				.add(Items.ORANGE_SHULKER_BOX)
				.add(Items.PINK_SHULKER_BOX)
				.add(Items.PURPLE_SHULKER_BOX)
				.add(Items.RED_SHULKER_BOX)
				.add(Items.WHITE_SHULKER_BOX)
				.add(Items.YELLOW_SHULKER_BOX)
				.add(Items.BLACK_SHULKER_BOX);
	}

	private void generateToolTags() {
		getOrCreateTagBuilder(CommonItemTags.AXES)
				.addOptionalTag(CommonItemTags.FABRIC_AXES.id())
				.add(Items.DIAMOND_AXE)
				.add(Items.GOLDEN_AXE)
				.add(Items.WOODEN_AXE)
				.add(Items.STONE_AXE)
				.add(Items.IRON_AXE)
				.add(Items.NETHERITE_AXE);
		getOrCreateTagBuilder(CommonItemTags.PICKAXES)
				.addOptionalTag(CommonItemTags.FABRIC_PICKAXES.id())
				.add(Items.DIAMOND_PICKAXE)
				.add(Items.GOLDEN_PICKAXE)
				.add(Items.WOODEN_PICKAXE)
				.add(Items.STONE_PICKAXE)
				.add(Items.IRON_PICKAXE)
				.add(Items.NETHERITE_PICKAXE);
		getOrCreateTagBuilder(CommonItemTags.HOES)
				.addOptionalTag(CommonItemTags.FABRIC_HOES.id())
				.add(Items.DIAMOND_HOE)
				.add(Items.GOLDEN_HOE)
				.add(Items.WOODEN_HOE)
				.add(Items.STONE_HOE)
				.add(Items.IRON_HOE)
				.add(Items.NETHERITE_HOE);
		getOrCreateTagBuilder(CommonItemTags.SWORDS)
				.addOptionalTag(CommonItemTags.FABRIC_SWORDS.id())
				.add(Items.DIAMOND_SWORD)
				.add(Items.GOLDEN_SWORD)
				.add(Items.WOODEN_SWORD)
				.add(Items.STONE_SWORD)
				.add(Items.IRON_SWORD)
				.add(Items.NETHERITE_SWORD);
		getOrCreateTagBuilder(CommonItemTags.SHOVELS)
				.addOptionalTag(CommonItemTags.FABRIC_SHOVELS.id())
				.add(Items.DIAMOND_SHOVEL)
				.add(Items.GOLDEN_SHOVEL)
				.add(Items.WOODEN_SHOVEL)
				.add(Items.STONE_SHOVEL)
				.add(Items.IRON_SHOVEL)
				.add(Items.NETHERITE_SHOVEL);
		getOrCreateTagBuilder(CommonItemTags.SHEARS)
				.add(Items.SHEARS);
		getOrCreateTagBuilder(CommonItemTags.SPEARS)
				.add(Items.TRIDENT);
		getOrCreateTagBuilder(CommonItemTags.BOWS)
				.add(Items.CROSSBOW)
				.add(Items.BOW);
	}
}
