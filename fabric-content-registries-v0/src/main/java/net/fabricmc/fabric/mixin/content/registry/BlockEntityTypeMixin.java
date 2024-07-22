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

package net.fabricmc.fabric.mixin.content.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import net.fabricmc.fabric.impl.content.registry.BlockEntityTypeHooks;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin implements BlockEntityTypeHooks {
	@Shadow
	@Final
	@Mutable
	private Set<Block> blocks;

	@Override
	public void fabric$addSupportedBlocks(Block... validBlocks) {
		if (!(blocks instanceof HashSet<Block>)) {
			blocks = new HashSet<>(blocks);
		}

		blocks.addAll(List.of(validBlocks));
	}
}
