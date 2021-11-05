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

package net.fabricmc.fabric.api.mininglevel.v1;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagFactory;

/**
 * Defines additional {@code mineable} tags for vanilla tools not covered by vanilla.
 *
 * <p>{@code mineable} tags specify which tools are able to break a block effectively and drop it.
 * Fabric API defines two additional {@code mineable} tags: {@link #SWORD_MINEABLE #fabric:mineable/sword}
 * and {@link #SHEARS_MINEABLE #fabric:mineable/shears}.
 */
public final class FabricMineableTags {
	/**
	 * Blocks in this tag ({@code #fabric:mineable/sword}) can be effectively mined with swords.
	 *
	 * <p>As swords have materials and mining levels, the mining level tags described in
	 * {@link MiningLevelManager} also apply.
	 */
	public static final Tag.Identified<Block> SWORD_MINEABLE = register("mineable/sword");

	/**
	 * Blocks in this tag ({@code #fabric:mineable/shears}) can be effectively mined with shears.
	 */
	public static final Tag.Identified<Block> SHEARS_MINEABLE = register("mineable/shears");

	/**
	 * Blocks in this tag ({@code #fabric:mineable/shears}) can be effectively mined with shears at a faster than normal rate.
	 */
	public static final Tag.Identified<Block> SHEARS_MINEABLE_FAST = register("mineable/shears_fast");

	/**
	 * Blocks in this tag ({@code #fabric:mineable/shears}) can be effectively mined with shears at a slower than normal rate.
	 */
	public static final Tag.Identified<Block> SHEARS_MINEABLE_SLOW = register("mineable/shears_slow");

	private FabricMineableTags() {
	}

	private static Tag.Identified<Block> register(String id) {
		return TagFactory.BLOCK.create(new Identifier("fabric", id));
	}
}
