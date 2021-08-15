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

package net.fabricmc.fabric.test.lookup.item;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;

/**
 * An item that may provide an arbitrary text for display.
 */
public interface Inspectable {
	/**
	 * @return A text to print when a player right-clicks the Inspector block with this item.
	 */
	Text inspect();

	ItemApiLookup<Inspectable, Void> LOOKUP =
			ItemApiLookup.get(new Identifier("testmod:inspectable"), Inspectable.class, Void.class);
}
