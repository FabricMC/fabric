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

package net.fabricmc.fabric.api.tool.attribute.v1;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.api.tag.TagRegistry;

/**
 * Tool item tags provided by Fabric.
 */
public class FabricToolTags {
	public static final Tag.Identified<Item> AXES_TAG = register("axes");
	public static final Tag.Identified<Item> HOES_TAG = register("hoes");
	public static final Tag.Identified<Item> PICKAXES_TAG = register("pickaxes");
	public static final Tag.Identified<Item> SHOVELS_TAG = register("shovels");
	public static final Tag.Identified<Item> SWORDS_TAG = register("swords");
	public static final Tag.Identified<Item> SHEARS_TAG = register("shears");

	@Deprecated
	public static final Tag<Item> AXES = AXES_TAG;
	@Deprecated
	public static final Tag<Item> HOES = HOES_TAG;
	@Deprecated
	public static final Tag<Item> PICKAXES = PICKAXES_TAG;
	@Deprecated
	public static final Tag<Item> SHOVELS = SHOVELS_TAG;
	@Deprecated
	public static final Tag<Item> SWORDS = SWORDS_TAG;
	@Deprecated
	public static final Tag<Item> SHEARS = SHEARS_TAG;

	private FabricToolTags() { }

	private static Tag.Identified<Item> register(String id) {
		return TagFactory.ITEM.create(new Identifier("fabric", id));
	}
}
