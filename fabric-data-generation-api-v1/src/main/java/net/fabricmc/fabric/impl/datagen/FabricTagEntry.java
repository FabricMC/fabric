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

package net.fabricmc.fabric.impl.datagen;

import java.util.function.Predicate;

import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricTagEntry<T> extends Tag.TagEntry {
	private static final TagManager TAG_MANAGER = RequiredTagListRegistry.createBuiltinTagManager();

	private final Registry<T> registry;
	private final Identifier id;
	private final boolean allowNonVanilla;

	public FabricTagEntry(Registry<T> registry, Identifier id, boolean allowNonVanilla) {
		super(id);

		this.registry = registry;
		this.id = id;
		this.allowNonVanilla = allowNonVanilla;
	}

	@Override
	public boolean canAdd(Predicate<Identifier> existenceTest, Predicate<Identifier> duplicationTest) {
		if (allowNonVanilla) {
			return true;
		}

		TagGroup<T> tagGroup = TAG_MANAGER.getOrCreateTagGroup(registry.getKey());
		return tagGroup.contains(id) || super.canAdd(existenceTest, duplicationTest);
	}
}
