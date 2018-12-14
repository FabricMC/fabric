package net.fabricmc.fabric.colormapper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorMapperRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockColorMapper;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColorMapper implements ClientModInitializer {
	private static final boolean ENABLED = true;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		if (!ENABLED) return;
		LOGGER.info("Initialising ColorMapper");

		// Redstone is now the same colour as grass
		ColorMapperRegistry.BLOCKS.register((block, pos, world, layer) -> {
			BlockColorMapper mapper = ColorMapperRegistry.BLOCKS.get(Blocks.GRASS);
			return mapper == null ? -1 : mapper.getColor(block, pos, world, layer);
		}, Blocks.REDSTONE_WIRE);

		// Make white dye glow red.
		ColorMapperRegistry.ITEMS.register((item, layer) ->
				(int) (64 * (Math.sin(System.currentTimeMillis() / 5e2) + 3)) << 16,
			Items.WHITE_DYE);
	}
}
