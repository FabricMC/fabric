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

package net.fabricmc.fabric.api.tools;

import net.minecraft.item.ItemStack;

/**
 * Interface for tools whose properties are affected by NBT. Goes on the item.
 */
public interface DynamicTool {
	/**
	 * @param stack The stack to check on.
	 * @return The mining level of the tool. 3 is equal to a diamond pick.
	 */
	int getMiningLevel(ItemStack stack);

	/**
	 * @param stack The stack to check on.
	 * @return The mining speed of the tool. 8.0 is equal to a diamond pick.
	 */
	float getMiningSpeed(ItemStack stack);
}
