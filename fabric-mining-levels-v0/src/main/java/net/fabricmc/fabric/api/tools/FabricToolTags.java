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

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

/**
 * Tool item tags provided by Fabric.
 *
 * @deprecated Use the moved {@link net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags} class instead
 */
@Deprecated
public class FabricToolTags {
	public static final Tag<Item> AXES = net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags.AXES;
	public static final Tag<Item> HOES = net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags.HOES;
	public static final Tag<Item> PICKAXES = net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags.PICKAXES;
	public static final Tag<Item> SHOVELS = net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags.SHOVELS;
	public static final Tag<Item> SWORDS = net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags.SWORDS;

	private FabricToolTags() { }
}
