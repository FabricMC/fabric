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

import java.util.function.BiFunction;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

/**
 * An extended version of {@link DimensionType} with automatic raw id management and default placement settings.
 * {@code FabricDimensionType} instances are constructed and registered through a {@link Builder}.
 *
 * @see #builder()
 * @see #getDefaultPlacement()
 * @see #getDesiredRawId()
 */
public final class FabricDimensionType extends DimensionType {
	private final EntityPlacer defaultPlacement;
	private int desiredRawId;
	/** The fixed raw id for this dimension type, set through reflection. */
	private int fixedRawId;

	/**
	 * Returns a new {@link Builder}.
	 */
	public static Builder builder() {
		return new FabricDimensionType.Builder();
	}

	/**
	 * @param suffix        the string suffix unique to the dimension type
	 * @param saveDir       the name of the save directory for the dimension type
	 * @param builder   	builder instance containing other parameters
	 * @see #builder()
	 */
	private FabricDimensionType(String suffix, String saveDir, Builder builder) {
		// Pass an arbitrary raw id that does not map to any vanilla dimension. That id should never get used.
		super(3, suffix, saveDir, builder.factory, builder.skyLight, builder.biomeAccessStrategy);
		this.defaultPlacement = builder.defaultPlacer;
	}

	/**
	 * Return the desired raw id of this dimension type.
	 *
	 * @return the preferred raw id of this dimension type
	 * @see Builder#desiredRawId(int)
	 */
	public int getDesiredRawId() {
		return desiredRawId;
	}

	/**
	 * Return the current raw id for this dimension type.
	 *
	 * <p>The returned id is guaranteed to be unique and persistent in a save,
	 * as well as synchronized between a server and its connected clients.
	 * It may change when connecting to a different server or opening a new save.
	 *
	 * @return the current raw id for this dimension type
	 * @see #getDesiredRawId()
	 */
	@Override
	public int getRawId() {
		return this.fixedRawId;
	}

	/**
	 * Return the default placement logic for this dimension. The returned placer
	 * never returns {@code null} when called.
	 *
	 * @return the default placement logic for this dimension
	 * @see FabricDimensions#teleport(Entity, DimensionType, EntityPlacer)
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
		private BiFunction<World, DimensionType, ? extends Dimension> factory;
		private int desiredRawId = 0;
		private boolean skyLight = true;
		private BiomeAccessType biomeAccessStrategy = VoronoiBiomeAccessType.INSTANCE;

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
		 * Set the factory used to create new {@link Dimension} instances of the built type.
		 * The dimension factory must be set before building a dimension type.
		 *
		 * @param factory a function creating new {@code Dimension} instances
		 * @return this {@code Builder} object
		 * @throws NullPointerException if {@code factory} is {@code null}
		 */
		public Builder factory(BiFunction<World, DimensionType, ? extends Dimension> factory) {
			Preconditions.checkNotNull(factory);

			this.factory = factory;
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

		/**
		 * Governs how biome information is retrieved from random seed and world coordinates.
		 * If this method is not called, value defaults to the three-dimensional strategy
		 * used by the End and Nether dimensions.
		 *
		 * @param biomeAccessStrategy Function to be used for biome generation.
		 * @return this {@code Builder} object
		 */
		public Builder biomeAccessStrategy(BiomeAccessType biomeAccessStrategy) {
			Preconditions.checkNotNull(biomeAccessStrategy);

			this.biomeAccessStrategy = biomeAccessStrategy;
			return this;
		}

		/**
		 * Sets this dimension's desired raw id.
		 * If this method is not called, the value defaults to the raw registry id
		 * of the dimension type.
		 *
		 * <p>A Fabric Dimension's desired raw id is used as its actual raw id
		 * when it does not conflict with any existing id, and the world
		 * save does not map the dimension to a different raw id.
		 *
		 * @param desiredRawId the new raw id for this dimension type
		 * @return this {@code Builder} object
		 * @apiNote Mods that used to have a dimension with a manually set id
		 * may use this method to set a default id corresponding to the old one,
		 * so as not to break compatibility with old worlds.
		 */
		public Builder desiredRawId(int desiredRawId) {
			this.desiredRawId = desiredRawId;
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
		 * @throws IllegalStateException    if no {@link #factory(BiFunction) factory} or {@link #defaultPlacer(EntityPlacer) default placer}
		 *                                  have been set
		 */
		public FabricDimensionType buildAndRegister(Identifier dimensionId) {
			Preconditions.checkArgument(Registry.DIMENSION_TYPE.get(dimensionId) == null);
			Preconditions.checkState(this.defaultPlacer != null, "No defaultPlacer has been specified!");
			Preconditions.checkState(this.factory != null, "No dimension factory has been specified!");

			String suffix = dimensionId.getNamespace() + "_" + dimensionId.getPath();
			String saveDir = "DIM_" + dimensionId.getNamespace() + "_" + dimensionId.getPath();
			FabricDimensionType built = new FabricDimensionType(suffix, saveDir, this);
			Registry.register(Registry.DIMENSION_TYPE, dimensionId, built);

			if (this.desiredRawId != 0) {
				built.desiredRawId = this.desiredRawId;
			} else {
				built.desiredRawId = Registry.DIMENSION_TYPE.getRawId(built) - 1;
			}

			built.fixedRawId = built.desiredRawId;
			return built;
		}
	}
}
