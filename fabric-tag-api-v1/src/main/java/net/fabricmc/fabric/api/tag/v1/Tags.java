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

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tag.TagDelegate;
import net.fabricmc.fabric.mixin.tag.BlockTagsAccessor;
import net.fabricmc.fabric.mixin.tag.EntityTypeTagsAccessor;
import net.fabricmc.fabric.mixin.tag.FluidTagsAccessor;
import net.fabricmc.fabric.mixin.tag.ItemTagsAccessor;

/**
 * Utilities related to the creation and registration of {@link Tag}s.
 * In Minecraft, there are two types of tags: "required" and "optional".
 *
 * <p>A "required" tag is required for the game to function, usually being bound to game logic. One example of this is whether {@link BlockTags#DRAGON_IMMUNE a block is immune to the ender dragon}.
 * If a required tag is not present, a Minecraft server will refuse to start and a client connecting to a server will disconnect.
 *
 * <p>An "optional" tag is not required for the game to function.
 *
 * @see Tag
 */
public final class Tags {
	/**
	 * Creates a thread-safe delegate to an "optional" tag which refers to a tag in a tag group.
	 * This type of tag will recompute the tag values if the tag being referred to is outdated or no longer exists.
	 *
	 * <p>If a tag of the specified {@code id} does not exist in the tag group, then delegate will refer to an empty tag.
	 *
	 * @param id the id of the tag
	 * @param groupSupplier a supplier used to get the tag group
	 * @param <T> the type of object the tag holds
	 * @return a new identified tag
	 */
	public static <T> Tag.Identified<T> createDelegatedTag(Identifier id, Supplier<TagGroup<T>> groupSupplier) {
		return TagDelegate.create(id, groupSupplier);
	}

	/**
	 * Registers a block tag.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
	 */
	public static Tag.Identified<Block> block(Identifier id) {
		return createDelegatedTag(id, BlockTags::getTagGroup);
	}

	/**
	 * Registers a required block tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking a {@link BlockTags#DRAGON_IMMUNE block dragon immune}.
	 *
	 * @param id the id of the tag
	 * @return a new required tag
	 */
	public static Tag.Identified<Block> requiredBlock(Identifier id) {
		return BlockTagsAccessor.register(id.toString()); // WTF Mojang
	}

	/**
	 * Registers an entity type tag.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
	 */
	public static Tag.Identified<EntityType<?>> entityType(Identifier id) {
		return createDelegatedTag(id, EntityTypeTags::getTagGroup);
	}

	/**
	 * Registers a required entity type tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking a type of entity as a {@link EntityTypeTags#RAIDERS raider}.
	 *
	 * @param id the id of the tag
	 * @return a new required tag
	 */
	public static Tag.Identified<EntityType<?>> requiredEntityType(Identifier id) {
		return EntityTypeTagsAccessor.register(id.toString()); // WTF Mojang
	}

	/**
	 * Registers a fluid tag.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
	 */
	public static Tag.Identified<Fluid> fluid(Identifier id) {
		return createDelegatedTag(id, FluidTagsAccessor.getRequiredTagList()::getGroup);
	}

	/**
	 * Registers a required fluid tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking a fluid as {@link FluidTags#LAVA lava}.
	 *
	 * @param id the id of the tag
	 * @return a new required tag
	 */
	public static Tag.Identified<Fluid> requiredFluid(Identifier id) {
		return FluidTagsAccessor.register(id.toString()); // WTF Mojang
	}

	/**
	 * Registers an item tag.
	 *
	 * @param id the id of the tag
	 * @return a tag
	 */
	public static Tag.Identified<Item> item(Identifier id) {
		return createDelegatedTag(id, ItemTags::getTagGroup);
	}

	/**
	 * Registers a required item tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking an item as {@link ItemTags#PIGLIN_REPELLENTS repulsive to a piglin}.
	 *
	 * @param id the id of the tag
	 * @return a new required tag
	 */
	public static Tag.Identified<Item> requiredItem(Identifier id) {
		return ItemTagsAccessor.register(id.toString()); // WTF Mojang
	}

	private Tags() {
	}
}
