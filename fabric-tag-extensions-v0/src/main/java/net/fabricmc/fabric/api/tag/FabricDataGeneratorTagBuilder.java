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

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * Interface implemented by {@link net.minecraft.data.server.AbstractTagProvider.ObjectBuilder} instances when
 * Fabric API is present. Useful for data generators.
 */
public interface FabricDataGeneratorTagBuilder<T> {
	/**
	 * Add an optional entry of type {@code <T>} to the tag.
	 * The object identified by {@code id} is not required to be present on load,
	 * which is useful for integration with other mods.
	 * @param id The ID of the object to add
	 * @see net.minecraft.data.server.AbstractTagProvider.ObjectBuilder#add(T) for the non-optional version of this method.
	 */
	void addOptionalObject(Identifier id);

	/**
	 * Add an optional tag entry to the tag.
	 * The tag identified by {@code id} is not required to be present on load,
	 * which is useful for integration with other mods.
	 * @param id The ID of the tag to add
	 * @see net.minecraft.data.server.AbstractTagProvider.ObjectBuilder#addTag(Tag.Identified)  for the non-optional version of this method.
	 */
	void addOptionalTag(Identifier id);
}
