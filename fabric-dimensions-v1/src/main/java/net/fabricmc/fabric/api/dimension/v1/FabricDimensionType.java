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

package net.fabricmc.fabric.api.dimension.v1;

import java.util.OptionalLong;
import java.util.function.Function;

import com.google.common.base.Preconditions;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

/**
 * An extended version of {@link DimensionType} with automatic raw id management and default placement settings.
 * {@code FabricDimensionType} instances are constructed and registered through a {@link Builder}.
 *
 * @see #builder()
 * @see #getDefaultPlacement()
 */
public final class FabricDimensionType extends DimensionType {
	private final EntityPlacer defaultPlacement;

	/**
	 * Returns a new {@link Builder}.
	 */
	public static Builder builder() {
		return new FabricDimensionType.Builder();
	}

	/**
	 * @param builder   	builder instance containing other parameters
	 * @see #builder()
	 */
	private FabricDimensionType(Builder builder) {
		// Pass an arbitrary raw id that does not map to any vanilla dimension. That id should never get used.
		super(OptionalLong.empty(), builder.skyLight, false, false, false, false, 0F);
		this.defaultPlacement = builder.defaultPlacer;
	}

	/**
	 * Return the default placement logic for this dimension. The returned placer
	 * never returns {@code null} when called.
	 *
	 * @return the default placement logic for this dimension
	 * @see FabricDimensions#teleport(Entity, RegistryKey, EntityPlacer)
	 */
	public EntityPlacer getDefaultPlacement() {
		return this.defaultPlacement;
	}

	/**
	 * A builder for creating and registering {@code FabricDimensionType} instances. Example: <pre>   {@code
	 *
	 *   public static final FabricDimensionType MY_DIMENSION
	 *       = FabricDimensionType.builder()
	 *           .defaultPlacement((oldEntity, destination, portalDir, horizontalOffset, verticalOffset) ->
	 *           		new BlockPattern.TeleportTarget(new Vec3d(0, 100, 0), teleported.getVelocity(), 0))
	 *           .factory(MyDimension::new)
	 *           .skyLight(true)
	 *           .buildAndRegister();}</pre>
	 *
	 * <p>Builder instances can be reused; it is safe to call {@link #buildAndRegister(Identifier)} multiple
	 * times (with different identifiers) to build and register multiple dimension types in series.
	 * Each new dimension type uses the settings of the builder at the time it is built.
	 *
	 * @see FabricDimensionType#builder()
	 */
	public static final class Builder {
		private EntityPlacer defaultPlacer;
		private boolean skyLight = true;
		private ChunkGenerator chunkGenerator;

		private Builder() {
		}

		/**
		 * Set the default placer used when teleporting entities to dimensions of the built type.
		 * The default placer must be set before building a dimension type.
		 *
		 * <p>A dimension type's default placer must never return {@code null} when its
		 * {@link EntityPlacer#placeEntity(Entity, ServerWorld, Direction, double, double) placeEntity} method
		 * is called.
		 *
		 * @param defaultPlacer a default entity placer for dimensions of the built type
		 * @return this {@code Builder} object
		 * @throws NullPointerException if {@code defaultPlacer} is {@code null}
		 */
		public Builder defaultPlacer(EntityPlacer defaultPlacer) {
			Preconditions.checkNotNull(defaultPlacer);

			this.defaultPlacer = defaultPlacer;
			return this;
		}

		/**
		 * Set whether built dimension types use skylight like the Overworld.
		 * If this method is not called, the value defaults to {@code true}.
		 *
		 * @param skyLight {@code true} if the dimension of the built type should use skylight,
		 *                 {@code false} otherwise
		 * @return this {@code Builder} object
		 */
		public Builder skyLight(boolean skyLight) {
			this.skyLight = skyLight;
			return this;
		}

		public Builder chunkGenerator(ChunkGenerator chunkGenerator) {
			this.chunkGenerator = chunkGenerator;
			return this;
		}

		/**
		 * Build and register a {@code FabricDimensionType}.
		 *
		 * <p>The {@code dimensionId} is used as a registry ID, and as
		 * a unique name both for the dimension suffix and the save directory.
		 *
		 * @param dimensionId the id used to name and register the dimension
		 * @return the built {@code FabricDimensionType}
		 * @throws IllegalArgumentException if an existing dimension has already been registered with {@code dimensionId}
		 * @throws IllegalStateException    if no {@link #factory(Function) factory} or {@link #defaultPlacer(EntityPlacer) default placer}
		 *                                  have been set
		 */
		public FabricDimensionType buildAndRegister(Identifier dimensionId) {
			Preconditions.checkState(this.defaultPlacer != null, "No defaultPlacer has been specified!");
			Preconditions.checkState(this.chunkGenerator != null, "No chunk generator has been specified!");

			FabricDimensionType dimensionType = new FabricDimensionType( this);
			Pair<DimensionType, ChunkGenerator> pair = Pair.of(dimensionType, chunkGenerator);
			FabricDimensionInternals.FABRIC_DIM_MAP.put(RegistryKey.getOrCreate(Registry.DIMENSION_TYPE_KEY, dimensionId), pair);
			return dimensionType;
		}
	}
}
