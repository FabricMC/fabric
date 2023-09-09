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

package net.fabricmc.fabric.impl.tag.convention.datagen.generators;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalFluidTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class FluidTagGenerator extends FabricTagProvider.FluidTagProvider {
	public FluidTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		getOrCreateTagBuilder(ConventionalFluidTags.WATER)
				.addOptionalTag(FluidTags.WATER);
		getOrCreateTagBuilder(ConventionalFluidTags.LAVA)
				.addOptionalTag(FluidTags.LAVA);
		getOrCreateTagBuilder(ConventionalFluidTags.MILK);
		getOrCreateTagBuilder(ConventionalFluidTags.HONEY);

		// Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
		// TODO: Remove backwards compat tag entries in 1.22
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalFluidTags.WATER);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalFluidTags.LAVA);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalFluidTags.MILK);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalFluidTags.HONEY);
	}

	private FabricTagBuilder getOrCreateTagBuilderWithOptionalLegacy(TagKey<Fluid> tag)
	{
		return getOrCreateTagBuilder(tag).addOptionalTag(new Identifier("c", tag.id().getPath()));
	}
}
