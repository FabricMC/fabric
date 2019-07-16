package net.fabric.test;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricRenderingTests implements ClientModInitializer {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initialising ColorProviderMod");

		// Redstone is now the same color as grass
		ColorProviderRegistry.BLOCK.register((block, world, pos, layer) -> {
			BlockColorProvider provider = ColorProviderRegistry.BLOCK.get(Blocks.GRASS);
			return provider == null ? -1 : provider.getColor(block, world, pos, layer);
		}, Blocks.REDSTONE_WIRE);

		// Make white dye glow red.
		ColorProviderRegistry.ITEM.register((item, layer) ->
			                                    (int) (64 * (Math.sin(System.currentTimeMillis() / 5e2) + 3)) << 16,
		                                    Items.WHITE_DYE);
	}
}
