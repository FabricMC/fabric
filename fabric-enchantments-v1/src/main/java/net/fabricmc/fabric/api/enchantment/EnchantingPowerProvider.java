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

package net.fabricmc.fabric.api.enchantment;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * To be used on blocks which increase the power of an enchanting table.
 */
public interface EnchantingPowerProvider {

	/**
	 * Returns the amount of how much to increase the enchanting power
	 * @param world The world of the block
	 * @param blockPos The block position of the block
	 * @return The power amount provided by this block
	 */
	int getEnchantingPower(World world, BlockPos blockPos);
}
