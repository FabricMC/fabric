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

package net.fabricmc.fabric.test.renderer.simple;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

/**
 * A simple testmod that renders a simple block rendered using the fabric renderer api.
 * The block that is rendered is a simple frame that another block is rendered in.
 * Blocks that provide a block entity cannot be placed inside the frame.
 *
 * <p>There are no fancy shaders or glow that is provided by this renderer test.
 */
public final class RendererTest implements ModInitializer {
	public static final FrameBlock[] FRAMES = new FrameBlock[] {
			new FrameBlock(id("frame")),
			new FrameBlock(id("frame_multipart")),
			new FrameBlock(id("frame_weighted")),
	};
	public static final BlockEntityType<FrameBlockEntity> FRAME_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(FrameBlockEntity::new, FRAMES).build(null);

	public static final Identifier PILLAR_ID = id("pillar");
	public static final Block PILLAR = new Block(FabricBlockSettings.of(Material.STONE));

	@Override
	public void onInitialize() {
		for (FrameBlock frameBlock : FRAMES) {
			Registry.register(Registry.BLOCK, frameBlock.id, frameBlock);
			Registry.register(Registry.ITEM, frameBlock.id, new BlockItem(frameBlock, new Item.Settings().group(ItemGroup.MISC)));
		}

		// To anyone testing this: pillars are supposed to connect vertically with each other.
		// Additionally, they should also connect vertically to frame blocks containing a pillar.
		// (The frame block will not change, but adjacent pillars should adjust their textures).
		Registry.register(Registry.BLOCK, PILLAR_ID, PILLAR);
		Registry.register(Registry.ITEM, PILLAR_ID, new BlockItem(PILLAR, new Item.Settings().group(ItemGroup.MISC)));

		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("frame"), FRAME_BLOCK_ENTITY);
	}

	public static Identifier id(String path) {
		return new Identifier("fabric-renderer-api-v1-testmod", path);
	}
}
