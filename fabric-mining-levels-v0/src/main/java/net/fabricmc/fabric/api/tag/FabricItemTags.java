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

package net.fabricmc.fabric.api.tag;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tools.FabricToolTags;

/**
 * Item tags provided by Fabric.
 *
 * @deprecated Use dedicated classes, such as {@link net.fabricmc.fabric.api.tools.FabricToolTags}
 */
@Deprecated
public class FabricItemTags {
	public static final Tag<Item> AXES = FabricToolTags.AXES;
	public static final Tag<Item> HOES = FabricToolTags.HOES;
	public static final Tag<Item> PICKAXES = FabricToolTags.PICKAXES;
	public static final Tag<Item> SHOVELS = FabricToolTags.SHOVELS;
	public static final Tag<Item> SWORDS = FabricToolTags.SWORDS;

	private FabricItemTags() { }

	private static Tag<Item> register(String id) {
		return TagRegistry.item(new Identifier("fabric", id));
	}
}
