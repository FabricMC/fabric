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

import net.minecraft.tag.TagKey;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Tool item tags provided by Fabric.
 */
public class FabricToolTags {
	public static final TagKey<Item> AXES = register("axes");
	public static final TagKey<Item> HOES = register("hoes");
	public static final TagKey<Item> PICKAXES = register("pickaxes");
	public static final TagKey<Item> SHOVELS = register("shovels");
	public static final TagKey<Item> SWORDS = register("swords");
	public static final TagKey<Item> SHEARS = register("shears");

	private FabricToolTags() { }

	private static TagKey<Item> register(String id) {
		return TagKey.intern(Registry.ITEM_KEY, new Identifier("fabric", id));
	}
}
