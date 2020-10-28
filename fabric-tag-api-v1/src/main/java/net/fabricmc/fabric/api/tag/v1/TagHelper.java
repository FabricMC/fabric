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

package net.fabricmc.fabric.api.tag.v1;

import net.minecraft.tag.Tag;

import net.fabricmc.fabric.impl.tag.FabricTagBuilderExtensions;
import net.fabricmc.fabric.impl.tag.FabricTagExtensions;

public final class TagHelper {
	private TagHelper() {
	}

	/**
	 * Checks if a tag has been replaced.
	 *
	 * @param tag the tag to check
	 * @return true if the tag has been replaced
	 */
	public static boolean hasBeenReplaced(Tag<?> tag) {
		if (tag instanceof FabricTagExtensions) {
			return ((FabricTagExtensions) tag).fabric_hasBeenReplaced();
		}

		return false; // Fallback if the impl doesn't implement our extensions
	}

	/**
	 * Clears all entries from this tag builder.
	 *
	 * @param builder the builder to clear.
	 * @return the builder for chaining.
	 */
	public static Tag.Builder clearEntries(Tag.Builder builder) {
		final FabricTagBuilderExtensions extensions = (FabricTagBuilderExtensions) builder;
		extensions.fabric_clearTagEntries();

		return builder;
	}
}
