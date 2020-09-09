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

package net.fabricmc.fabric.api.component.accessor.v1;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Sub-type of {@code ComponentContext} for block-based providers,
 * carries information needed for provider to retrieve the component.
 */
public interface BlockComponentContext extends ComponentContext {
	/**
	 * Locates the block component within the world.
	 *
	 * @return Positing of the block component within the world.
	 */
	BlockPos pos();

	/**
	 * {@code BlockEntity} instance at {@link #pos()}, or {@code null} if there is none.
	 * This value is lazily retrieved and cached and in some cases may be pre-populated.
	 * Performance will be slightly improved using this method instead of retrieving the
	 * block entity from the world directly.
	 *
	 * @return {@code BlockEntity} instance at {@link #pos()}, or {@code null} if there is none
	 */
	/* @Nullable */ BlockEntity blockEntity();

	/**
	 * {@code Block} instance at {@link #pos()}. Will never be {@code null} but could be air.
	 *
	 * @return @code Block} instance at {@link #pos()}
	 */
	Block block();

	/**
	 * {@code BlockState} instance at {@link #pos()}. Will never be {@code null} but could be air.
	 *
	 * @return @code BlockState} instance at {@link #pos()}
	 */
	BlockState blockState();
}
