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

package net.fabricmc.fabric.api.client.rendereregistry.v1.item;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * This interface allows you to modify the count label that is displayed on item stacks in inventories.
 */
@Environment(EnvType.CLIENT)
public interface ItemLabelProperties {
	/**
	 * Checks if the count label is visible or not.
	 * @param stack stack to check
	 * @param override label contents override
	 * @return {@code true} if label is visible, {@code false} otherwise
	 */
	boolean isVisible(ItemStack stack, String override);

	/**
	 * Gets the contents of the count label.
	 * @param stack stack to check
	 * @param override label contents override
	 * @return label contents
	 */
	Text getContents(ItemStack stack, String override);

	/**
	 * Gets the color of the count label.
	 * @param stack stack to check
	 * @param override label contents override
	 * @return label color
	 */
	int getColor(ItemStack stack, String override);
}
