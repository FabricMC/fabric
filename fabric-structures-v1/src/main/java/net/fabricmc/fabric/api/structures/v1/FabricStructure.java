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

package net.fabricmc.fabric.api.structures.v1;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * A general class that provides guaranteed extension of key methods.
 */
public abstract class FabricStructure<T extends FeatureConfig> extends StructureFeature<T> {
	protected FabricStructure(Codec<T> codec) {
		super(codec);
	}

	/**
	 * The helper methods mean this method ought to be the original definition of the name for a structure.
	 *
	 * <p>Is overridden to add namespaces to the names where possible</p>
	 */
	@Override
	public String getName() {
		Identifier id = Registry.STRUCTURE_FEATURE.getId(this);
		return id == null ? super.getName() : id.toString();
	}

	/**
	 * A method to easily allow structures to define what generation step they generate in.
	 *
	 * @return The generation step the structure ought to generate in
	 */
	@Override
	public abstract GenerationStep.Feature method_28663();
}
