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

package net.fabricmc.fabric.api.object.builder.v1.block;

import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;

/**
 * <p>Implements a basic {@link FluidBlock}.</p>
 *
 * <p>This class is a wrapper of {@link FluidBlock} with an accessible public constructor.</p>
 */
public class FabricFluidBlock extends FluidBlock {
	/**
	 * Initializes a new {@link FabricFluidBlock} instance.
	 *
	 * @param fluid    Linked fluid.
	 * @param settings Fluid settings.
	 */
	public FabricFluidBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}
}
