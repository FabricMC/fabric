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
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

/**
 * A simple testmod that renders a simple block rendered using the fabric renderer api.
 * The block that is rendered is a simple frame that another block is rendered in.
 * Blocks that provide a block entity cannot be placed inside the frame.
 *
 * <p>There are no fancy shaders or glow that is provided by this renderer test.
 */
public final class RendererTest implements ModInitializer {
	public static final Block FRAME = new FrameBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());
	public static final BlockEntityType<FrameBlockEntity> FRAME_BLOCK_ENTITY = BlockEntityType.Builder.create(FrameBlockEntity::new, FRAME).build(null);

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("fabric-renderer-api-v1-testmod", "frame"), FRAME);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("fabric-renderer-api-v1-testmod", "frame"), FRAME_BLOCK_ENTITY);
	}
}
