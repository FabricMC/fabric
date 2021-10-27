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

package net.fabricmc.fabric.api.fluid.v1.tag;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Provides default fabric fluid tags.
 */
public class FabricFluidTags {
	private static final List<Tag<Fluid>> TAGS;
	public static final Tag<Fluid> FABRIC_FLUID;

	private FabricFluidTags() {}

	public static List<Tag<Fluid>> getFluidTags() {
		return TAGS;
	}

	static {
		TAGS = Lists.newArrayList();
		FABRIC_FLUID = TagFactory.FLUID.create(new Identifier("fabric", "fabric_fluid"));
		TAGS.add(FluidTags.WATER);
		TAGS.add(FluidTags.LAVA);
		TAGS.add(FABRIC_FLUID);
	}
}
