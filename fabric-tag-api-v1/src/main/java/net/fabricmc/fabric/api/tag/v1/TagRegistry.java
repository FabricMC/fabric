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

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tag.TagDelegate;
import net.fabricmc.fabric.mixin.tag.BlockTagsAccessor;
import net.fabricmc.fabric.mixin.tag.EntityTypeTagsAccessor;
import net.fabricmc.fabric.mixin.tag.FluidTagsAccessor;
import net.fabricmc.fabric.mixin.tag.ItemTagsAccessor;

public final class TagRegistry {
	/**
	 * Registers a block tag.
	 *
	 * @param id the id of the tag
	 * @return a tag
	 */
	public static Tag.Identified<Block> block(Identifier id) {
		return TagDelegate.create(id, BlockTags::getTagGroup);
	}

	/**
	 * Registers a required block tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking a {@link BlockTags#DRAGON_IMMUNE block dragon immune}.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
	 */
	public static Tag.Identified<Block> requiredBlock(Identifier id) {
		return BlockTagsAccessor.register(id.toString()); // WTF Mojang
	}

	/**
	 * Registers an entity type tag.
	 *
	 * @param id the id of the tag
	 * @return a tag
	 */
	public static Tag.Identified<EntityType<?>> entityType(Identifier id) {
		return TagDelegate.create(id, EntityTypeTags::getTagGroup);
	}

	/**
	 * Registers a required entity type tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking a type of entity as a {@link EntityTypeTags#RAIDERS raider}.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
	 */
	public static Tag.Identified<EntityType<?>> requiredEntityType(Identifier id) {
		return EntityTypeTagsAccessor.register(id.toString()); // WTF Mojang
	}

	/**
	 * Registers a fluid tag.
	 *
	 * @param id the id of the tag
	 * @return a tag
	 */
	public static Tag.Identified<Fluid> fluid(Identifier id) {
		return TagDelegate.create(id, FluidTagsAccessor.getRequiredTagList()::getGroup);
	}

	/**
	 * Registers a required fluid tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking a fluid as {@link FluidTags#LAVA lava}.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
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
		return TagDelegate.create(id, ItemTags::getTagGroup);
	}

	/**
	 * Registers a required item tag.
	 *
	 * <p>A required tag must be present for clients to connect to a server and for a server to start.
	 * This type of tag is typically used when game logic is bound to a tag, such as marking an item as {@link ItemTags#PIGLIN_REPELLENTS repulsive to a piglin}.
	 *
	 * @param id the id of the tag
	 * @return an identified tag
	 */
	public static Tag.Identified<Item> requiredItem(Identifier id) {
		return ItemTagsAccessor.register(id.toString()); // WTF Mojang
	}

	private TagRegistry() {
	}
}
