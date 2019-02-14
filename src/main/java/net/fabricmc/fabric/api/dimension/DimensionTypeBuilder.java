/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.dimension;

import net.fabricmc.fabric.impl.dimension.FabricDimensionComponents;
import net.fabricmc.fabric.impl.registry.RemappableRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

import java.util.function.BiFunction;

public class DimensionTypeBuilder {

	private Identifier identifier;
	private int id;
	private BiFunction<World, DimensionType, ? extends Dimension> dimensionFactory = OverworldDimension::new;
	private boolean hasSkyLight = true;
	private EntityTeleporter teleporter = EntityTeleporter.DEFAULT_TELEPORTER;

	private DimensionTypeBuilder(Identifier identifier, int id) {
		if(Registry.DIMENSION.contains(identifier)){
			throw new RuntimeException("Dimension already exists with name " + identifier.toString());
		}
		if(Registry.DIMENSION.getInt(id) != null){
			throw new RuntimeException("Dimension already exists with id " + id);
		}
		this.identifier = identifier;
		this.id = id;
	}

	/**
	 *
	 * Creates a new DimensionTypeBuilder
	 *
	 * @param identifier the identifier will be used as the name of the DimensionType and will also be used for the save directory
	 * @return a DimensionTypeBuilder
	 */
	public static DimensionTypeBuilder create(Identifier identifier, int id){
		return new DimensionTypeBuilder(identifier, id);
	}

	public DimensionTypeBuilder factory(BiFunction<World, DimensionType, ? extends Dimension> dimensionFactory) {
		this.dimensionFactory = dimensionFactory;
		return this;
	}

	/**
	 *
	 * Used to set if the DimensionType has skyLight enabled (default true)
	 *
	 * @param hasSkyLight
	 * @return a DimensionTypeBuilder
	 */
	public DimensionTypeBuilder hasSkyLight(boolean hasSkyLight) {
		this.hasSkyLight = hasSkyLight;
		return this;
	}

	public DimensionTypeBuilder teleporter(EntityTeleporter teleporter){
		this.teleporter = teleporter;
		return this;
	}

	private String getDimensionName(){
		return identifier.getNamespace() + "_" + identifier.getPath();
	}

	/**
	 *
	 * Create the Dimension Type
	 *
	 * @return an instance of {@link DimensionType}
	 */
	public DimensionType build(){
		DimensionType dimensionType = new FabricDimensionType(id, getDimensionName(), "DIM_" + getDimensionName(), dimensionFactory, hasSkyLight);
		Registry.set(Registry.DIMENSION, id, identifier.toString(), dimensionType);
		FabricDimensionComponents.INSTANCE.addModdedDimension(dimensionType, teleporter);
		return dimensionType;
	}

	//Only needed while we dont have at's
	private static class FabricDimensionType extends DimensionType {

		protected FabricDimensionType(int id,
		                              String suffix,
		                              String saveDir,
		                              BiFunction<World, DimensionType, ? extends Dimension> factory, boolean hasSkyLight) {
			super(id, suffix, saveDir, factory, hasSkyLight);
		}
	}
}
