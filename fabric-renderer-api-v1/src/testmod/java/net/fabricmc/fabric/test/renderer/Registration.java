/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.renderer;

import java.util.function.Function;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public final class Registration {
	public static final FrameBlock FRAME_BLOCK = register("frame", FrameBlock::new, AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
	public static final FrameBlock FRAME_MULTIPART_BLOCK = register("frame_multipart", FrameBlock::new, AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
	public static final FrameBlock FRAME_VARIANT_BLOCK = register("frame_variant", FrameBlock::new, AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
	public static final Block PILLAR_BLOCK = register("pillar", Block::new, AbstractBlock.Settings.create());
	public static final Block OCTAGONAL_COLUMN_BLOCK = register("octagonal_column", OctagonalColumnBlock::new, AbstractBlock.Settings.create().nonOpaque().strength(1.8F));
	public static final Block RIVERSTONE_BLOCK = register("riverstone", Block::new, AbstractBlock.Settings.copy(Blocks.STONE));

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
	public static final Item RIVERSTONE_ITEM = register("riverstone", new BlockItem(RIVERSTONE_BLOCK, new Item.Settings()));

	public static final BlockEntityType<FrameBlockEntity> FRAME_BLOCK_ENTITY_TYPE = register("frame", FabricBlockEntityTypeBuilder.create(FrameBlockEntity::new, FRAME_BLOCKS).build());

	// see also Blocks#register, which is functionally the same
	private static <T extends Block> T register(String path, Function<AbstractBlock.Settings, T> constructor, AbstractBlock.Settings settings) {
		Identifier id = RendererTest.id(path);
		return Registry.register(Registries.BLOCK, id, constructor.apply(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))));
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
