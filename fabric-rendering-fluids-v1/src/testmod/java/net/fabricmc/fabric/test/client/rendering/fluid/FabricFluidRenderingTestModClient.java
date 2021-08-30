package net.fabricmc.fabric.test.client.rendering.fluid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

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

		FluidRenderHandlerRegistry.INSTANCE.register(TestFluids.NO_OVERLAY, new ColoredFluidRenderer(
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_still"),
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_flowing"),
			0xFF5555
		));

		FluidRenderHandlerRegistry.INSTANCE.register(TestFluids.NO_OVERLAY_FLOWING, new ColoredFluidRenderer(
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_still"),
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_flowing"),
			0xFF5555
		));

		FluidRenderHandlerRegistry.INSTANCE.register(TestFluids.OVERLAY, new ColoredFluidRenderer(
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_still"),
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_flowing"),
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_overlay"),
			0x5555FF
		));

		FluidRenderHandlerRegistry.INSTANCE.register(TestFluids.OVERLAY_FLOWING, new ColoredFluidRenderer(
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_still"),
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_flowing"),
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_overlay"),
			0x5555FF
		));

		FluidRenderHandlerRegistry.INSTANCE.register(TestFluids.CUSTOM, new CustomizedFluidRenderer(
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_overlay")
		));

		FluidRenderHandlerRegistry.INSTANCE.register(TestFluids.CUSTOM_FLOWING, new CustomizedFluidRenderer(
			new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_overlay")
		));

		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_still"));
			registry.register(new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_flowing"));
			registry.register(new Identifier("fabric-rendering-fluids-v1-testmod:block/test_fluid_overlay"));
		});
	}
}
