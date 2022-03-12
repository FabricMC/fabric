package net.fabricmc.fabric.impl.common.tag.datagen.generators;

import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tags.v1.CommonEntityTypeTags;

public class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
	public EntityTypeTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(CommonEntityTypeTags.BOSSES)
				.add(EntityType.ENDER_DRAGON)
				.add(EntityType.WITHER);
		getOrCreateTagBuilder(CommonEntityTypeTags.MINECARTS)
				.add(EntityType.MINECART)
				.add(EntityType.TNT_MINECART)
				.add(EntityType.CHEST_MINECART)
				.add(EntityType.FURNACE_MINECART)
				.add(EntityType.COMMAND_BLOCK_MINECART)
				.add(EntityType.HOPPER_MINECART)
				.add(EntityType.SPAWNER_MINECART);
		getOrCreateTagBuilder(CommonEntityTypeTags.BOATS)
				.add(EntityType.BOAT);
	}
}
