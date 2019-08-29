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
 * Interface for items which are capable of mining or breaking blocks. Supports dynamic mining levels/speeds.
 * Does not need to extend {@link net.minecraft.item.MiningToolItem}, but should be in any {@link FabricToolTags}.
 * If your item extends MiningToolItem, but does not have dynamic stats, you do not need to implement this.
 */
public interface DynamicMiningStats {
	/**
	 * @param stack The stack to check on.
	 * @return The mining level of the item. 3 is equal to a diamond pick.
	 */
	int getMiningLevel(ItemStack stack);

	/**
	 * @param stack The stack to check on.
	 * @return The mining speed of the item. 8.0 is equal to a diamond pick.
	 */
	float getMiningSpeed(ItemStack stack);
}
