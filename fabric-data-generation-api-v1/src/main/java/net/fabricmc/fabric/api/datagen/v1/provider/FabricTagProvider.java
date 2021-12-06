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

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * @deprecated use {@link FabricTagsProvider} instead.
 */
@Deprecated
public abstract class FabricTagProvider<T> extends FabricTagsProvider<T> {
	protected FabricTagProvider(FabricDataGenerator dataGenerator, Registry<T> registry, String path, String name) {
		super(dataGenerator, registry, path, name);
	}

	/**
	 * @deprecated use {@link BlockTagsProvider} instead.
	 */
	@Deprecated
	public abstract static class BlockTagProvider extends BlockTagsProvider {
		public BlockTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}
	}

	/**
	 * @deprecated use {@link ItemTagsProvider} instead.
	 */
	@Deprecated
	public abstract static class ItemTagProvider extends ItemTagsProvider {
		public ItemTagProvider(FabricDataGenerator dataGenerator, @Nullable BlockTagsProvider blockTagProvider) {
			super(dataGenerator, blockTagProvider);
		}

		public ItemTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}
	}

	/**
	 * @deprecated use {@link FluidTagsProvider} instead.
	 */
	@Deprecated
	public abstract static class FluidTagProvider extends FluidTagsProvider {
		public FluidTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}
	}

	/**
	 * @deprecated use {@link EntityTypeTagsProvider} instead.
	 */
	@Deprecated
	public abstract static class EntityTypeTagProvider extends EntityTypeTagsProvider {
		public EntityTypeTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}
	}

	/**
	 * @deprecated use {@link GameEventTagsProvider} instead.
	 */
	@Deprecated
	public abstract static class GameEventTagProvider extends GameEventTagsProvider {
		public GameEventTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}
	}
}
