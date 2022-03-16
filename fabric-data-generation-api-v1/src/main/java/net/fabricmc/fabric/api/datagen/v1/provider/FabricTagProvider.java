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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.fabric.impl.datagen.ForcedTagEntry;
import net.fabricmc.fabric.mixin.datagen.DynamicRegistryManagerAccessor;

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
 * @see DynamicRegistryTagProvider
 */
public abstract class FabricTagProvider<T> extends AbstractTagProvider<T> {
	private final FabricDataGenerator fabricDataGenerator;
	private final String path;

	/**
	 * Construct a new {@link FabricTagProvider} with the default computed path.
	 *
	 * <p>Common implementations of this class are provided. For example @see BlockTagProvider
	 *
	 * @param dataGenerator The data generator instance
	 * @param registry The backing registry for the Tag type.
	 * @param name The name used for {@link DataProvider#getName()}
	 */
	protected FabricTagProvider(FabricDataGenerator dataGenerator, Registry<T> registry) {
		this(dataGenerator, registry, TagManagerLoader.getPath(registry.getKey()));
	}

	/**
	 * Construct a new {@link FabricTagProvider}.
	 *
	 * <p>Common implementations of this class are provided. For example @see BlockTagProvider
	 *
	 * @param dataGenerator The data generator instance
	 * @param registry The backing registry for the Tag type.
	 * @param path The directory name to write the tag file names. Example: "blocks" or "items"
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected FabricTagProvider(FabricDataGenerator dataGenerator, Registry<T> registry, String path) {
		super(dataGenerator, registry);
		this.fabricDataGenerator = dataGenerator;
		this.path = path.startsWith("tags/") ? path : "tags/" + path;

		if (!(this instanceof DynamicRegistryTagProvider) && BuiltinRegistries.REGISTRIES.contains((RegistryKey) registry.getKey())) {
			throw new IllegalArgumentException("Using FabricTagProvider to generate dynamic registry tags is not supported, Use DynamicRegistryTagProvider instead.");
		}
	}

	/**
	 * Implement this method and then use {@link FabricTagProvider#getOrCreateTagBuilder} to get and register new tag builders.
	 */
	protected abstract void generateTags();

	/**
	 * Creates a new instance of {@link FabricTagBuilder} for the given {@link TagKey} tag.
	 *
	 * @param tag The {@link TagKey} tag to create the builder for
	 * @return The {@link FabricTagBuilder} instance
	 */
	@Override
	protected FabricTagBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
		return new FabricTagBuilder<>(super.getOrCreateTagBuilder(tag));
	}

	@Override
	protected Path getOutput(Identifier id) {
		return this.root.getOutput().resolve("data/%s/%s/%s.json".formatted(id.getNamespace(), path, id.getPath()));
	}

	@Override
	protected final void configure() {
		generateTags();
	}

	/**
	 * Extend this class to create {@link Block} tags in the "/blocks" tag directory.
	 */
	public abstract static class BlockTagProvider extends FabricTagProvider<Block> {
		public BlockTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.BLOCK, "Block Tags");
		}
	}

	/**
	 * Extend this class to create {@link Item} tags in the "/items" tag directory.
	 */
	public abstract static class ItemTagProvider extends FabricTagProvider<Item> {
		@Nullable
		private final Function<TagKey<Block>, Tag.Builder> blockTagBuilderProvider;

		/**
		 * Construct an {@link ItemTagProvider} tag provider <b>with</b> an associated {@link BlockTagProvider} tag provider.
		 *
		 * @param dataGenerator a {@link ItemTagProvider} tag provider
		 */
		public ItemTagProvider(FabricDataGenerator dataGenerator, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
			super(dataGenerator, Registry.ITEM, "Item Tags");

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
		 * <p>Any block ids that do not exist in the item registry will be filtered out automatically.
		 *
		 * @param blockTag The block tag to copy from.
		 * @param itemTag The item tag to copy to.
		 */
		public void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
			Tag.Builder blockTagBuilder = Objects.requireNonNull(this.blockTagBuilderProvider, "Pass Block tag provider via constructor to use copy").apply(blockTag);
			Tag.Builder itemTagBuilder = this.getTagBuilder(itemTag);
			blockTagBuilder.streamEntries().filter((entry) -> entry.entry().canAdd(this.registry::containsId, (id) -> true)).forEach(itemTagBuilder::add);
		}
	}

	/**
	 * Extend this class to create {@link Fluid} tags in the "/fluids" tag directory.
	 */
	public abstract static class FluidTagProvider extends FabricTagProvider<Fluid> {
		public FluidTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.FLUID, "Fluid Tags");
		}
	}

	/**
	 * Extend this class to create {@link EntityType} tags in the "/entity_types" tag directory.
	 */
	public abstract static class EntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {
		public EntityTypeTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.ENTITY_TYPE, "Entity Type Tags");
		}
	}

	/**
	 * Extend this class to create {@link GameEvent} tags in the "/game_events" tag directory.
	 */
	public abstract static class GameEventTagProvider extends FabricTagProvider<GameEvent> {
		public GameEventTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.GAME_EVENT, "Game Event Tags");
		}
	}

	/**
	 * Extend this class to create dynamic registry tags.
	 */
	public abstract static class DynamicRegistryTagProvider<T> extends FabricTagProvider<T> {
		/**
		 * Construct a new {@link DynamicRegistryTagProvider}.
		 *
		 * @param dataGenerator The data generator instance
		 * @param registryKey The registry key of the dynamic registry
		 * @param path The directory name to write the tag file names
		 * @param name The name used for {@link DataProvider#getName()}
		 * @throws IllegalArgumentException if the registry is static registry
		 */
		protected DynamicRegistryTagProvider(FabricDataGenerator dataGenerator, RegistryKey<? extends Registry<T>> registryKey, String path) {
			super(dataGenerator, FabricDataGenHelper.getFakeDynamicRegistry(), path);
			Preconditions.checkArgument(DynamicRegistryManagerAccessor.getInfos().containsKey(registryKey), "Only dynamic registries are supported in this tag provider.");
		}
	}

	/**
	 * An extension to {@link net.minecraft.data.server.AbstractTagProvider.ObjectBuilder} that provides additional functionality.
	 */
	public final class FabricTagBuilder<T> extends ObjectBuilder<T> {
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
		 * @throws UnsupportedOperationException if the provider is an instance of {@link DynamicRegistryTagProvider}
		 * @see #add(Identifier)
		 */
		@Override
		public FabricTagBuilder<T> add(T element) {
			assertStaticRegistry();
			parent.add(element);
			return this;
		}

		/**
		 * Add a single element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> add(Identifier id) {
			builder.add(id, source);
			return this;
		}

		/**
		 * Add a single element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> add(RegistryKey<? extends T> registryKey) {
			return add(registryKey.getValue());
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
		 * Add an optional {@link RegistryKey} to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> addOptional(RegistryKey<? extends T> registryKey) {
			return addOptional(registryKey.getValue());
		}

		/**
		 * Add another tag to this tag.
		 *
		 * <p><b>Note:</b> any vanilla tags can be added to the builder,
		 * but other tags can only be added if it has a builder registered in the same provider.
		 *
		 * <p>Use {@link #forceAddTag(TagKey)} to force add any tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 * @see BlockTags
		 * @see EntityTypeTags
		 * @see FluidTags
		 * @see GameEventTags
		 * @see ItemTags
		 */
		@Override
		public FabricTagBuilder<T> addTag(TagKey<T> tag) {
			builder.addTag(tag.id(), source);
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

		/**
		 * Add another optional tag to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> addOptionalTag(TagKey<T> tag) {
			return addOptionalTag(tag.id());
		}

		/**
		 * Add another tag to this tag, ignoring any warning.
		 *
		 * <p><b>Note:</b> only use this method if you sure that the tag will be always available at runtime.
		 * If not, use {@link #addOptionalTag(Identifier)} instead.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> forceAddTag(TagKey<T> tag) {
			builder.add(new ForcedTagEntry(new Tag.TagEntry(tag.id())), source);
			return this;
		}

		/**
		 * Add multiple elements to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 * @throws UnsupportedOperationException if the provider is an instance of {@link DynamicRegistryTagProvider}
		 */
		@SafeVarargs
		@Override
		public final FabricTagBuilder<T> add(T... elements) {
			assertStaticRegistry();

			for (T element : elements) {
				add(element);
			}

			return this;
		}

		/**
		 * Add multiple elements to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder<T> add(Identifier... ids) {
			for (Identifier id : ids) {
				add(id);
			}

			return this;
		}

		/**
		 * Add multiple elements to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@SafeVarargs
		public final FabricTagBuilder<T> add(RegistryKey<T>... registryKeys) {
			for (RegistryKey<? extends T> registryKey : registryKeys) {
				add(registryKey);
			}

			return this;
		}

		private void assertStaticRegistry() {
			if (FabricTagProvider.this instanceof DynamicRegistryTagProvider) {
				throw new UnsupportedOperationException("Adding object instances is not supported for DynamicRegistryTagProvider.");
			}
		}
	}

	public FabricDataGenerator getFabricDataGenerator() {
		return fabricDataGenerator;
	}
}
