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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.data.server.tag.AbstractTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagBuilder;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLoader;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.fabric.impl.datagen.ForcedTagEntry;

/**
 * Implement this class (or one of the inner classes) to generate a tag list.
 *
 * <p>Register your implementation using {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
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
	/**
	 * Construct a new {@link FabricTagProvider} with the default computed path.
	 *
	 * <p>Common implementations of this class are provided. For example @see BlockTagProvider
	 *
	 * @param output        The {@link FabricDataOutput} instance
	 * @param registriesFuture      The backing registry for the Tag type.
	 */
	public FabricTagProvider(FabricDataOutput output, RegistryKey<? extends Registry<T>> registryKey, CompletableFuture<CommandRegistryWrapper.class_7874> registriesFuture) {
		super(output, registryKey, registriesFuture);
	}

	/**
	 * Implement this method and then use {@link FabricTagProvider#getOrCreateTagBuilder} to get and register new tag builders.
	 */
	protected abstract void configure(CommandRegistryWrapper.class_7874 arg);

	/**
	 * Override to enable adding objects to the tag builder directly.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected RegistryKey<T> reverseLookup(T element) {
		var registry = Registry.REGISTRIES.get((RegistryKey) field_40957);
		if (registry != null) {
			var key = registry.getKey(element);
			if (key.isPresent()) {
				return (RegistryKey<T>) key.get();
			}
		}

		throw new UnsupportedOperationException("Adding objects is not supported by " + getClass());
	}

	/**
	 * Creates a new instance of {@link FabricTagBuilder} for the given {@link TagKey} tag.
	 *
	 * @param tag The {@link TagKey} tag to create the builder for
	 * @return The {@link FabricTagBuilder} instance
	 */
	@Override
	protected FabricTagBuilder getOrCreateTagBuilder(TagKey<T> tag) {
		return new FabricTagBuilder(super.getOrCreateTagBuilder(tag));
	}

	/**
	 * Extend this class to create {@link Block} tags in the "/blocks" tag directory.
	 */
	public abstract static class BlockTagProvider extends FabricTagProvider<Block> {
		public BlockTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> registriesFuture) {
			super(output, Registry.BLOCK_KEY, registriesFuture);
		}

		@Override
		protected RegistryKey<Block> reverseLookup(Block element) {
			return element.getRegistryEntry().registryKey();
		}
	}

	/**
	 * Extend this class to create {@link Item} tags in the "/items" tag directory.
	 */
	public abstract static class ItemTagProvider extends FabricTagProvider<Item> {
		@Nullable
		private final Function<TagKey<Block>, TagBuilder> blockTagBuilderProvider;

		/**
		 * Construct an {@link ItemTagProvider} tag provider <b>with</b> an associated {@link BlockTagProvider} tag provider.
		 *
		 * @param output The {@link FabricDataOutput} instance
		 */
		public ItemTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> completableFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
			super(output, Registry.ITEM_KEY, completableFuture);

			this.blockTagBuilderProvider = blockTagProvider == null ? null : blockTagProvider::getTagBuilder;
		}

		/**
		 * Construct an {@link ItemTagProvider} tag provider <b>without</b> an associated {@link BlockTagProvider} tag provider.
		 *
		 * @param output The {@link FabricDataOutput} instance
		 */
		public ItemTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> completableFuture) {
			this(output, completableFuture, null);
		}

		/**
		 * Copy the entries from a tag with the {@link Block} type into this item tag.
		 *
		 * <p>The {@link ItemTagProvider} tag provider must be constructed with an associated {@link BlockTagProvider} tag provider to use this method.
		 *
		 *
		 * @param blockTag The block tag to copy from.
		 * @param itemTag  The item tag to copy to.
		 */
		public void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
			TagBuilder blockTagBuilder = Objects.requireNonNull(this.blockTagBuilderProvider, "Pass Block tag provider via constructor to use copy").apply(blockTag);
			TagBuilder itemTagBuilder = this.getTagBuilder(itemTag);
			blockTagBuilder.build().forEach(itemTagBuilder::add);
		}

		@Override
		protected RegistryKey<Item> reverseLookup(Item element) {
			return element.getRegistryEntry().registryKey();
		}
	}

	/**
	 * Extend this class to create {@link Fluid} tags in the "/fluids" tag directory.
	 */
	public abstract static class FluidTagProvider extends FabricTagProvider<Fluid> {
		public FluidTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> completableFuture) {
			super(output, Registry.FLUID_KEY, completableFuture);
		}

		@Override
		protected RegistryKey<Fluid> reverseLookup(Fluid element) {
			return element.getRegistryEntry().registryKey();
		}
	}

	/**
	 * Extend this class to create {@link Enchantment} tags in the "/enchantments" tag directory.
	 */
	public abstract static class EnchantmentTagProvider extends FabricTagProvider<Enchantment> {
		public EnchantmentTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> completableFuture) {
			super(output, Registry.ENCHANTMENT_KEY, completableFuture);
		}

		@Override
		protected RegistryKey<Enchantment> reverseLookup(Enchantment element) {
			return Registry.ENCHANTMENT.getKey(element)
					.orElseThrow(() -> new IllegalArgumentException("Enchantment " + element + " is not registered"));
		}
	}

	/**
	 * Extend this class to create {@link EntityType} tags in the "/entity_types" tag directory.
	 */
	public abstract static class EntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {
		public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> completableFuture) {
			super(output, Registry.ENTITY_TYPE_KEY, completableFuture);
		}

		@Override
		protected RegistryKey<EntityType<?>> reverseLookup(EntityType<?> element) {
			return element.getRegistryEntry().registryKey();
		}
	}

	/**
	 * Extend this class to create {@link GameEvent} tags in the "/game_events" tag directory.
	 */
	public abstract static class GameEventTagProvider extends FabricTagProvider<GameEvent> {
		public GameEventTagProvider(FabricDataOutput output, CompletableFuture<CommandRegistryWrapper.class_7874> completableFuture) {
			super(output, Registry.GAME_EVENT_KEY, completableFuture);
		}

		@Override
		protected RegistryKey<GameEvent> reverseLookup(GameEvent element) {
			return element.getRegistryEntry().registryKey();
		}
	}

	/**
	 * An extension to {@link ObjectBuilder} that provides additional functionality.
	 */
	public final class FabricTagBuilder extends ObjectBuilder<T> {
		private final AbstractTagProvider.ObjectBuilder<T> parent;

		private FabricTagBuilder(ObjectBuilder<T> parent) {
			super(parent.builder);
			this.parent = parent;
		}

		/**
		 * Set the value of the `replace` flag in a Tag.
		 *
		 * <p>When set to true the tag will replace any existing tag entries.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder setReplace(boolean replace) {
			((net.fabricmc.fabric.impl.datagen.FabricTagBuilder) builder).fabric_setReplace(replace);
			return this;
		}

		/**
		 * Add an element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder add(T element) {
			add(reverseLookup(element));
			return this;
		}

		/**
		 * Add multiple elements to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@SafeVarargs
		public final FabricTagBuilder add(T... element) {
			Stream.of(element).map(FabricTagProvider.this::reverseLookup).forEach(this::add);
			return this;
		}

		/**
		 * Add an element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 * @see #add(Identifier)
		 */
		public FabricTagBuilder add(RegistryKey<T> registryKey) {
			parent.method_46835(registryKey);
			return this;
		}

		/**
		 * Add a single element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 * @see #add(Identifier)
		 */
		@Override
		public FabricTagBuilder method_46835(RegistryKey<T> registryKey) {
			parent.method_46835(registryKey);
			return this;
		}

		/**
		 * Add a single element to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder add(Identifier id) {
			builder.add(id);
			return this;
		}

		/**
		 * Add an optional {@link Identifier} to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@Override
		public FabricTagBuilder addOptional(Identifier id) {
			parent.addOptional(id);
			return this;
		}

		/**
		 * Add an optional {@link RegistryKey} to the tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder addOptional(RegistryKey<? extends T> registryKey) {
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
		public FabricTagBuilder addTag(TagKey<T> tag) {
			builder.addTag(tag.id());
			return this;
		}

		/**
		 * Add another optional tag to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		@Override
		public FabricTagBuilder addOptionalTag(Identifier id) {
			parent.addOptionalTag(id);
			return this;
		}

		/**
		 * Add another optional tag to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder addOptionalTag(TagKey<T> tag) {
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
		public FabricTagBuilder forceAddTag(TagKey<T> tag) {
			builder.add(new ForcedTagEntry(TagEntry.create(tag.id())));
			return this;
		}

		/**
		 * Add multiple elements to this tag.
		 *
		 * @return the {@link FabricTagBuilder} instance
		 */
		public FabricTagBuilder add(Identifier... ids) {
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
		@Override
		public final FabricTagBuilder add(RegistryKey<T>... registryKeys) {
			for (RegistryKey<T> registryKey : registryKeys) {
				add(registryKey);
			}

			return this;
		}
	}
}
