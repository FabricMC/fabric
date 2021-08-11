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
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;

/**
 * Helper methods for registering Tags.
 *
 * @deprecated use {@link TagFactory} instead.
 */
@Deprecated
public final class TagRegistry {
	private TagRegistry() { }

	public static <T> Tag.Identified<T> create(Identifier id, Supplier<TagGroup<T>> containerSupplier) {
		return new TagDelegate<>(id, containerSupplier);
	}

	/**
	 * @deprecated use {@link TagFactory#BLOCK}
	 */
	@Deprecated
	public static Tag<Block> block(Identifier id) {
		return TagFactory.BLOCK.create(id);
	}

	/**
	 * @deprecated use {@link TagFactory#ENTITY_TYPE}
	 */
	@Deprecated
	public static Tag<EntityType<?>> entityType(Identifier id) {
		return TagFactory.ENTITY_TYPE.create(id);
	}

	/**
	 * @deprecated use {@link TagFactory#FLUID}
	 */
	@Deprecated
	public static Tag<Fluid> fluid(Identifier id) {
		return TagFactory.FLUID.create(id);
	}

	/**
	 * @deprecated use {@link TagFactory#ITEM}
	 */
	@Deprecated
	public static Tag<Item> item(Identifier id) {
		return TagFactory.ITEM.create(id);
	}
}
