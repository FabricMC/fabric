package net.fabricmc.fabric.test.client.rendering.fluid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.Blocks;

public class FabricFluidRenderingTestModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Doors now will have overlay textures to the side
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.ACACIA_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.DARK_OAK_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.BIRCH_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.CRIMSON_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.IRON_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.JUNGLE_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.OAK_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.SPRUCE_DOOR, true);
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.WARPED_DOOR, true);

		// Red stained glass will have falling fluid textures to the side
		FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(Blocks.RED_STAINED_GLASS, false);
	}
}
