/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface ArmorItemNamespaced {
	/**
	 * Returns the namespaced armor texture location.
	 *
	 * @param stack The ItemStack.
	 * @param layer The requested layer. Can be 1 or 2.
	 * @param suffix The suffix requested by the vanilla renderer, or "" if no suffix was specified.
	 * @return The texture Identifier, as a full .PNG pat.h
	 */
	Identifier getNamespacedArmorTexture(ItemStack stack, int layer, String suffix);
}
