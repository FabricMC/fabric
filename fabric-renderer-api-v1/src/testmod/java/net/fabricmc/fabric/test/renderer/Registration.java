package net.fabricmc.fabric.test.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public final class Registration {
	public static final FrameBlock FRAME_BLOCK = register("frame", new FrameBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
	public static final FrameBlock FRAME_MULTIPART_BLOCK = register("frame_multipart", new FrameBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
	public static final FrameBlock FRAME_VARIANT_BLOCK = register("frame_variant", new FrameBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
	public static final Block PILLAR_BLOCK = register("pillar", new Block(FabricBlockSettings.create()));
	public static final Block OCTAGONAL_COLUMN_BLOCK = register("octagonal_column", new Block(FabricBlockSettings.create().nonOpaque().strength(1.8F)));

	public static final FrameBlock[] FRAME_BLOCKS = new FrameBlock[] {
			FRAME_BLOCK,
			FRAME_MULTIPART_BLOCK,
			FRAME_VARIANT_BLOCK,
	};

	public static final Item FRAME_ITEM = register("frame", new BlockItem(FRAME_BLOCK, new Item.Settings()));
	public static final Item FRAME_MULTIPART_ITEM = register("frame_multipart", new BlockItem(FRAME_MULTIPART_BLOCK, new Item.Settings()));
	public static final Item FRAME_VARIANT_ITEM = register("frame_variant", new BlockItem(FRAME_VARIANT_BLOCK, new Item.Settings()));
	public static final Item PILLAR_ITEM = register("pillar", new BlockItem(PILLAR_BLOCK, new Item.Settings()));
	public static final Item OCTAGONAL_COLUMN_ITEM = register("octagonal_column", new BlockItem(OCTAGONAL_COLUMN_BLOCK, new Item.Settings()));

	public static final BlockEntityType<FrameBlockEntity> FRAME_BLOCK_ENTITY_TYPE = register("frame", FabricBlockEntityTypeBuilder.create(FrameBlockEntity::new, FRAME_BLOCKS).build(null));

	private static <T extends Block> T register(String path, T block) {
		return Registry.register(Registries.BLOCK, RendererTest.id(path), block);
	}

	private static <T extends Item> T register(String path, T item) {
		return Registry.register(Registries.ITEM, RendererTest.id(path), item);
	}

	private static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, RendererTest.id(path), blockEntityType);
	}

	public static void init() {
	}
}
