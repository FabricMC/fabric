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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tag.TagDelegate;
import net.fabricmc.fabric.mixin.tag.FluidTagsAccessor;

/**
 * Helper methods for obtaining tag references.
 */
public final class TagRegistry {
	private TagRegistry() {
	}

	/**
	 * Obtains a tag reference that can automatically update across changes such as resource
	 * reloads, server packet updates, etc.
	 *
	 * @param id                the tag's identifier
	 * @param containerSupplier the supplier of the latest version of the tag container
	 * @param <T>               the tag's content type
	 * @return the reliable tag reference
	 */
	public static <T> Tag<T> create(Identifier id, Supplier<? extends TagContainer<T>> containerSupplier) {
		return new TagDelegate<T>(id, null) {
			// weak reference to prevent leaks after tags have been reloaded
			private WeakReference<Map<Identifier, Tag<T>>> entries = new WeakReference<>(null);

			@Override
			protected void onAccess() {
				// container object references don't change; entries map object references do!
				TagContainer<T> currContainer = containerSupplier.get();
				Map<Identifier, Tag<T>> currentEntries = currContainer.getEntries();

				if (entries.get() != currentEntries) {
					entries = new WeakReference<>(currentEntries);
					delegate = currContainer.getOrCreate(this.getId());
				}

				Preconditions.checkNotNull(currentEntries, "current entries");
				Preconditions.checkNotNull(delegate, "delegate");
			}
		};
	}

	/**
	 * Gets a reliable reference to a block tag.
	 *
	 * <p>Compared to the tags directly obtained from the server's tag manager,
	 * this tag automatically updates across the server's data reloads and across
	 * reception of tag update packets if the client is connected to a remote server.
	 *
	 * @param id the tag's identifier
	 * @return the auto updating tag
	 */
	public static Tag<Block> block(Identifier id) {
		return create(id, BlockTags::getContainer);
	}

	/**
	 * Gets a reliable reference to an entity type tag.
	 *
	 * <p>Compared to the tags directly obtained from the server's tag manager,
	 * this tag automatically updates across the server's data reloads and across
	 * reception of tag update packets if the client is connected to a remote server.
	 *
	 * @param id the tag's identifier
	 * @return the auto updating tag
	 */
	public static Tag<EntityType<?>> entityType(Identifier id) {
		return create(id, EntityTypeTags::getContainer);
	}

	/**
	 * Gets a reliable reference to a fluid tag.
	 *
	 * <p>Compared to the tags directly obtained from the server's tag manager,
	 * this tag automatically updates across the server's data reloads and across
	 * reception of tag update packets if the client is connected to a remote server.
	 *
	 * @param id the tag's identifier
	 * @return the auto updating tag
	 */
	public static Tag<Fluid> fluid(Identifier id) {
		return create(id, FluidTagsAccessor::getContainer);
	}

	/**
	 * Gets a reliable reference to an item tag.
	 *
	 * <p>Compared to the tags directly obtained from the server's tag manager,
	 * this tag automatically updates across the server's data reloads and across
	 * reception of tag update packets if the client is connected to a remote server.
	 *
	 * @param id the tag's identifier
	 * @return the auto updating tag
	 */
	public static Tag<Item> item(Identifier id) {
		return create(id, ItemTags::getContainer);
	}

	/**
	 * Gets a reliable reference to a function tag.
	 *
	 * <p>Compared to the tags directly obtained from the server's command function manager,
	 * this tag automatically updates across the server's data reloads, but it does not persist
	 * across startup and shutdown of different servers.
	 *
	 * <p><b>Warning:</b> this reference does not synchronize across different server instances!
	 * Make sure you discard this reference when your current <i>Minecraft</i> server is stopped.
	 *
	 * @param server the <i>Minecraft</i> server
	 * @param id     the tag's identifier
	 * @return the auto updating tag
	 */
	public static Tag<CommandFunction> function(MinecraftServer server, Identifier id) {
		return create(id, server.getCommandFunctionManager()::getTags);
	}
}
