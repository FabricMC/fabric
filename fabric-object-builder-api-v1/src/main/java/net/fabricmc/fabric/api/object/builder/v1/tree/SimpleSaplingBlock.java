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

package net.fabricmc.fabric.api.object.builder.v1.tree;

import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;

/**
 * Allows you to create your own {@link SaplingBlock}s.
 *
 * <p>Example:<br>
 * {@code public static final SaplingBlock MY_SAPLING = new SimpleSaplingBlock(new MySaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));}
 *
 * @apiNote This is a temporary solution because access wideners cannot be applied to dependents yet.
 */
public class SimpleSaplingBlock extends SaplingBlock {
	public SimpleSaplingBlock(SaplingGenerator generator, Settings settings) {
		super(generator, settings);
	}
}
