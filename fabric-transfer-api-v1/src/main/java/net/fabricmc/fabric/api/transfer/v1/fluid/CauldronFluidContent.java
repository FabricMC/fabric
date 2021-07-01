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

package net.fabricmc.fabric.api.transfer.v1.fluid;

import java.util.Collection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.IntProperty;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.impl.transfer.fluid.CauldronStorage;

/**
 * Entrypoint to expose cauldrons to the Fluid Transfer API.
 * Empty, water and lava cauldrons are registered by default, and additional cauldrons must be registered with {@link #registerCauldron}.
 * Contents can be queried with {@link #getForBlock} and {@link #getForFluid}.
 *
 * <p>The {@code CauldronFluidContent} itself defines:
 * <ul>
 *     <li>The block of the cauldron.</li>
 *     <li>The fluid that can be accepted by the cauldron. NBT is discarded when entering the cauldron.</li>
 *     <li>Which fluid amounts can be stored in the cauldron, and how they map to the level property of the cauldron.
 *     If {@code levelProperty} is {@code null}, then {@code minLevel = maxLevel = 1}, and there is only one level.
 *     Otherwise, the levels are all the integer values between {@code minLevel} and {@code maxLevel} (included).
 *     </li>
 *     <li>{@code amountPerLevel} defines how much fluid (in droplets) there is in one level of the cauldron.</li>
 * </ul>
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public record CauldronFluidContent(Block block, Fluid fluid, long amountPerLevel, int minLevel, int maxLevel, @Nullable IntProperty levelProperty) {
	// Copy-on-write, identity semantics, null-checked.
	private static final ApiProviderMap<Block, CauldronFluidContent> blockToCauldron = ApiProviderMap.create();
	private static final ApiProviderMap<Fluid, CauldronFluidContent> fluidToCauldron = ApiProviderMap.create();

	/**
	 * Get the cauldron fluid content for a cauldron block, or {@code null} if none was registered (yet).
	 */
	@Nullable
	public static CauldronFluidContent getForBlock(Block block) {
		return blockToCauldron.get(block);
	}

	/**
	 * Get the cauldron fluid content for a fluid, or {@code null} if no cauldron was registered for that fluid (yet).
	 */
	@Nullable
	public static CauldronFluidContent getForFluid(Fluid fluid) {
		return fluidToCauldron.get(fluid);
	}

	/**
	 * Attempt to register a new cauldron if not already registered, allowing it to be filled and emptied through the Fluid Transfer API.
	 * In both cases, return the content of the cauldron, either the existing one, or the newly registered one.
	 *
	 * @param block The block of the cauldron.
	 * @param fluid The fluid stored in this cauldron.
	 * @param amountPerLevel How much fluid is contained in one level of the cauldron, in {@linkplain FluidConstants droplets}.
	 * @param levelProperty The property used by the cauldron to store its levels. {@code null} if the cauldron only has one level.
	 */
	public static synchronized CauldronFluidContent registerCauldron(Block block, Fluid fluid, long amountPerLevel, @Nullable IntProperty levelProperty) {
		CauldronFluidContent existingBlockData = blockToCauldron.get(block);

		if (existingBlockData != null) {
			return existingBlockData;
		}

		if (fluidToCauldron.get(fluid) != null) {
			throw new IllegalArgumentException("Fluid already has a mapping for a different block."); // TODO better message
		}

		CauldronFluidContent data;

		if (levelProperty == null) {
			data = new CauldronFluidContent(block, fluid, amountPerLevel, 1, 1, null);
		} else {
			Collection<Integer> levels = levelProperty.getValues();

			if (levels.size() == 0) {
				// TODO: throw exception
			}

			int minLevel = Integer.MAX_VALUE;
			int maxLevel = 0;

			for (int level : levels) {
				minLevel = Math.min(minLevel, level);
				maxLevel = Math.max(maxLevel, level);
			}

			if (minLevel != 1 || maxLevel < 1) {
				throw new IllegalStateException();
			}

			data = new CauldronFluidContent(block, fluid, amountPerLevel, minLevel, maxLevel, levelProperty);
		}

		blockToCauldron.putIfAbsent(block, data);
		fluidToCauldron.putIfAbsent(fluid, data);

		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> CauldronStorage.get(world, pos), block);

		return data;
	}

	/**
	 * Return the current level of the cauldron given its block state, or 0 if it's an empty cauldron.
	 */
	public int currentLevel(BlockState state) {
		if (fluid == Fluids.EMPTY) {
			return 0;
		} else if (levelProperty == null) {
			return 1;
		} else {
			return state.get(levelProperty);
		}
	}

	static {
		// Vanilla registrations
		CauldronFluidContent.registerCauldron(Blocks.CAULDRON, Fluids.EMPTY, FluidConstants.BUCKET, null);
		CauldronFluidContent.registerCauldron(Blocks.WATER_CAULDRON, Fluids.WATER, FluidConstants.BOTTLE, LeveledCauldronBlock.LEVEL);
		CauldronFluidContent.registerCauldron(Blocks.LAVA_CAULDRON, Fluids.LAVA, FluidConstants.BUCKET, null);
	}
}
