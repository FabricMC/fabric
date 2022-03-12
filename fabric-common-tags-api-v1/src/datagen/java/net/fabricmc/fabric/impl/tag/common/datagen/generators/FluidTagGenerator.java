package net.fabricmc.fabric.impl.tag.common.datagen.generators;

import net.minecraft.tag.FluidTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.v1.CommonFluidTags;

public class FluidTagGenerator extends FabricTagProvider.FluidTagProvider {
	public FluidTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(CommonFluidTags.WATER)
				.addOptionalTag(FluidTags.WATER);
		getOrCreateTagBuilder(CommonFluidTags.LAVA)
				.addOptionalTag(FluidTags.LAVA);
	}
}
