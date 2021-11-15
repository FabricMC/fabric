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
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;

public abstract class FabricTagProvider<T> extends AbstractTagProvider<T> {
	private final String path;
	private final String name;

	protected FabricTagProvider(FabricDataGenerator root, Registry<T> registry, String path, String name) {
		super(root, registry);
		this.path = path;
		this.name = name;
	}

	protected abstract void generateTags();

	@Override
	protected FabricObjectBuilder<T> getOrCreateTagBuilder(Tag.Identified<T> tag) {
		return new FabricObjectBuilder<>(super.getOrCreateTagBuilder(tag));
	}

	@Override
	protected Path getOutput(Identifier id) {
		return this.root.getOutput().resolve("data/%s/tags/%s/%s.json".formatted(id.getNamespace(), path, id.getPath()));
	}

	@Override
	protected void configure() {
		generateTags();
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract static class Blocks extends FabricTagProvider<Block> {
		public Blocks(FabricDataGenerator root) {
			super(root, Registry.BLOCK, "blocks", "Block Tags");
		}
	}

	public abstract static class Items extends FabricTagProvider<Item> {
		@Nullable
		private final Function<Tag.Identified<Block>, Tag.Builder> blockTagBuilderProvider;

		public Items(FabricDataGenerator root, @Nullable Blocks blockTagProvider) {
			super(root, Registry.ITEM, "items", "Item Tags");

			this.blockTagBuilderProvider = blockTagProvider == null ? null : blockTagProvider::getTagBuilder;
		}

		public Items(FabricDataGenerator root) {
			this(root, null);
		}

		public void copy(Tag.Identified<Block> blockTag, Tag.Identified<Item> itemTag) {
			Tag.Builder itemTagBuilder = this.getTagBuilder(itemTag);
			Tag.Builder blockTagBuilder = Objects.requireNonNull(this.blockTagBuilderProvider, "Pass Block tag provider via constructor to use copy").apply(blockTag);
			blockTagBuilder.streamEntries().forEach(itemTagBuilder::add);
		}
	}

	public abstract static class Fluids extends FabricTagProvider<Fluid> {
		public Fluids(FabricDataGenerator root) {
			super(root, Registry.FLUID, "fluids", "Fluid Tags");
		}
	}

	public abstract static class EntityTypes extends FabricTagProvider<EntityType<?>> {
		public EntityTypes(FabricDataGenerator root) {
			super(root, Registry.ENTITY_TYPE, "entity_types", "Entity Type Tags");
		}
	}

	public abstract static class GameEvents extends FabricTagProvider<GameEvent> {
		public GameEvents(FabricDataGenerator root) {
			super(root, Registry.GAME_EVENT, "game_events", "Game Event Tags");
		}
	}

	public static class FabricObjectBuilder<T> extends ObjectBuilder<T> {
		private final AbstractTagProvider.ObjectBuilder<T> parent;

		private FabricObjectBuilder(ObjectBuilder<T> parent) {
			super(parent.builder, parent.registry, parent.source);
			this.parent = parent;
		}

		public FabricObjectBuilder<T> setReplace(boolean replace) {
			((FabricTagBuilder) builder).fabric_setReplace(replace);
			return this;
		}

		@Override
		public FabricObjectBuilder<T> add(T element) {
			parent.add(element);
			return this;
		}

		@Override
		public FabricObjectBuilder<T> addOptional(Identifier id) {
			parent.addOptional(id);
			return this;
		}

		@Override
		public FabricObjectBuilder<T> addTag(Tag.Identified<T> identifiedTag) {
			parent.addTag(identifiedTag);
			return this;
		}

		@Override
		public FabricObjectBuilder<T> addOptionalTag(Identifier id) {
			parent.addOptionalTag(id);
			return this;
		}
	}
}
