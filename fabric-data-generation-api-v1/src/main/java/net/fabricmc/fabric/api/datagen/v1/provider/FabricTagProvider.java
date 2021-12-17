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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Implement this class (or one of the inner classes) to generate a tag list.
 *
 * <p>Register your implementation using {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 *
 * <p>Commonly used implementations of this class are provided:
 *
 * @see BlockTagProvider
 * @see ItemTagProvider
 * @see FluidTagProvider
 * @see EntityTypeTagProvider
 * @see GameEventTagProvider
 */
public abstract class FabricTagProvider<T> extends AbstractTagProvider<T> {
	private final String path;
	private final String name;

	/**
	 * Construct a new {@link FabricTagProvider}.
	 *
	 * <p>Common implementations of this class are provided. For example @see BlockTagProvider
	 *
	 * @param dataGenerator The data generator instance
	 * @param registry The backing registry for the Tag type.
	 * @param path The directory name to write the tag file names. Example: "blocks" or "items"
	 * @param name The name used for {@link DataProvider#getName()}
	 */
	protected FabricTagProvider(FabricDataGenerator dataGenerator, Registry<T> registry, String path, String name) {
		super(dataGenerator, registry);
		this.path = path;
		this.name = name;
	}

	/**
	 * Implement this method and then use {@link FabricTagProvider#getOrCreateTagBuilder} to get and register new tag builders.
	 */
	protected abstract void generateTags();

	/**
	 * Creates a new instance of {@link FabricTagBuilder} for the given {@link net.minecraft.tag.Tag.Identified} tag.
	 *
	 * @param tag The {@link net.minecraft.tag.Tag.Identified} tag to create the builder for
	 * @return The {@link FabricTagBuilder} instance
	 */
	@Override
	protected FabricTagBuilder<T> getOrCreateTagBuilder(Tag.Identified<T> tag) {
		return new FabricTagBuilder<>(super.getOrCreateTagBuilder(tag));
	}

	@Override
	protected Path getOutput(Identifier id) {
		return this.root.getOutput().resolve("data/%s/tags/%s/%s.json".formatted(id.getNamespace(), path, id.getPath()));
	}

	@Override
	protected final void configure() {
		generateTags();
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Extend this class to create {@link Block} tags in the "/blocks" tag directory.
	 */
	public abstract static class BlockTagProvider extends FabricTagProvider<Block> {
		public BlockTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.BLOCK, "blocks", "Block Tags");
		}
	}

	/**
	 * Extend this class to create {@link Item} tags in the "/items" tag directory.
	 */
	public abstract static class ItemTagProvider extends FabricTagProvider<Item> {
		@Nullable
		private final Function<Tag.Identified<Block>, Tag.Builder> blockTagBuilderProvider;

		/**
		 * Construct an {@link ItemTagProvider} tag provider <b>with</b> an associated {@link BlockTagProvider} tag provider.
		 *
		 * @param dataGenerator a {@link ItemTagProvider} tag provider
		 */
		public ItemTagProvider(FabricDataGenerator dataGenerator, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
			super(dataGenerator, Registry.ITEM, "items", "Item Tags");

			this.blockTagBuilderProvider = blockTagProvider == null ? null : blockTagProvider::getTagBuilder;
		}

		/**
		 * Construct an {@link ItemTagProvider} tag provider <b>without</b> an associated {@link BlockTagProvider} tag provider.
		 *
		 * @param dataGenerator a {@link ItemTagProvider} tag provider
		 */
		public ItemTagProvider(FabricDataGenerator dataGenerator) {
			this(dataGenerator, null);
		}

		/**
		 * Copy the entries from a tag with the {@link Block} type into this item tag.
		 *
		 * <p>The {@link ItemTagProvider} tag provider must be constructed with an associated {@link BlockTagProvider} tag provider to use this method.
		 *
		 * @param blockTag The block tag to copy from.
		 * @param itemTag The item tag to copy to.
		 */
		public void copy(Tag.Identified<Block> blockTag, Tag.Identified<Item> itemTag) {
			Tag.Builder blockTagBuilder = Objects.requireNonNull(this.blockTagBuilderProvider, "Pass Block tag provider via constructor to use copy").apply(blockTag);
			Tag.Builder itemTagBuilder = this.getTagBuilder(itemTag);
			blockTagBuilder.streamEntries().filter((entry) -> entry.getEntry().canAdd(this.registry::containsId, this.tagBuilders::containsKey)).forEach(itemTagBuilder::add);
		}
	}

	/**
	 * Extend this class to create {@link Fluid} tags in the "/fluids" tag directory.
	 */
	public abstract static class FluidTagProvider extends FabricTagProvider<Fluid> {
		public FluidTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.FLUID, "fluids", "Fluid Tags");
		}
	}

	/**
	 * Extend this class to create {@link EntityType} tags in the "/entity_types" tag directory.
	 */
	public abstract static class EntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {
		public EntityTypeTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.ENTITY_TYPE, "entity_types", "Entity Type Tags");
		}
	}

	/**
	 * Extend this class to create {@link GameEvent} tags in the "/game_events" tag directory.
	 */
	public abstract static class GameEventTagProvider extends FabricTagProvider<GameEvent> {
		public GameEventTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.GAME_EVENT, "game_events", "Game Event Tags");
		}
	}

	/**
	 * An extension to {@link net.minecraft.data.server.AbstractTagProvider.ObjectBuilder} that provides additional functionality.
	 */
	public static class FabricTagBuilder<T> extends ObjectBuilder<T> {
		private final AbstractTagProvider.ObjectBuilder<T> parent;

		private FabricTagBuilder(ObjectBuilder<T> parent) {
			super(parent.builder, parent.registry, parent.source);
			this.parent = parent;
		}

		/**
		 * Set the value of the `replace` flag in a Tag.
		 *
		 * <p>When set to true the tag will replace any existing tag entries.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> setReplace(boolean replace) {
			((net.fabricmc.fabric.impl.datagen.FabricTagBuilder) builder).fabric_setReplace(replace);
			return this;
		}

		/**
		 * Add a single element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@Override
		public FabricTagBuilder<T> add(T element) {
			parent.add(element);
			return this;
		}

		/**
		 * Add an optional {@link Identifier} to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@Override
		public FabricTagBuilder<T> addOptional(Identifier id) {
			parent.addOptional(id);
			return this;
		}

		/**
		 * Add another tag to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@Override
		public FabricTagBuilder<T> addTag(Tag.Identified<T> tag) {
			parent.addTag(tag);
			return this;
		}

		/**
		 * Add another tag to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> addTag(Tag<T> tag) {
			parent.addTag((Tag.Identified<T>) tag);
			return this;
		}

		/**
		 * Add another optional tag to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@Override
		public FabricTagBuilder<T> addOptionalTag(Identifier id) {
			parent.addOptionalTag(id);
			return this;
		}
	}
}
