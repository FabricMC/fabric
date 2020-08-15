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

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tag.TagGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;

/**
 * Helper methods for registering Tags.
 */
public final class TagRegistry {
	private TagRegistry() { }

	public static <T> Tag.Identified<T> create(Identifier id, Supplier<TagGroup<T>> containerSupplier) {
		return new TagDelegate<>(id, containerSupplier);
	}

	public static Tag<Block> block(Identifier id) {
		return create(id, BlockTags::getTagGroup);
	}

	public static Tag<EntityType<?>> entityType(Identifier id) {
		return create(id, EntityTypeTags::getTagGroup);
	}

	public static Tag<Fluid> fluid(Identifier id) {
		return create(id, () -> AccessorFluidTags.getRequiredTags().getGroup());
	}

	public static Tag<Item> item(Identifier id) {
		return create(id, ItemTags::getTagGroup);
	}
}
