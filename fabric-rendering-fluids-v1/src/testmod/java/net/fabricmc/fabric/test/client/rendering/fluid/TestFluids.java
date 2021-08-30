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

package net.fabricmc.fabric.test.client.rendering.fluid;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.registry.Registry;

public class TestFluids {
	public static final NoOverlayFluid NO_OVERLAY = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:no_overlay", new NoOverlayFluid.Still());
	public static final NoOverlayFluid NO_OVERLAY_FLOWING = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:no_overlay_flowing", new NoOverlayFluid.Flowing());

	public static final FluidBlock NO_OVERLAY_BLOCK = Registry.register(Registry.BLOCK, "fabric-rendering-fluids-v1-testmod:no_overlay", new FluidBlock(NO_OVERLAY, AbstractBlock.Settings.copy(Blocks.WATER)) {
	});

	public static final OverlayFluid OVERLAY = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:overlay", new OverlayFluid.Still());
	public static final OverlayFluid OVERLAY_FLOWING = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:overlay_flowing", new OverlayFluid.Flowing());

	public static final FluidBlock OVERLAY_BLOCK = Registry.register(Registry.BLOCK, "fabric-rendering-fluids-v1-testmod:overlay", new FluidBlock(OVERLAY, AbstractBlock.Settings.copy(Blocks.WATER)) {
	});

	public static final UnregisteredFluid UNREGISTERED = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:unregistered", new UnregisteredFluid.Still());
	public static final UnregisteredFluid UNREGISTERED_FLOWING = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:unregistered_flowing", new UnregisteredFluid.Flowing());

	public static final FluidBlock UNREGISTERED_BLOCK = Registry.register(Registry.BLOCK, "fabric-rendering-fluids-v1-testmod:unregistered", new FluidBlock(UNREGISTERED, AbstractBlock.Settings.copy(Blocks.WATER)) {
	});

	public static final CustomFluid CUSTOM = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:custom", new CustomFluid.Still());
	public static final CustomFluid CUSTOM_FLOWING = Registry.register(Registry.FLUID, "fabric-rendering-fluids-v1-testmod:custom_flowing", new CustomFluid.Flowing());

	public static final FluidBlock CUSTOM_BLOCK = Registry.register(Registry.BLOCK, "fabric-rendering-fluids-v1-testmod:custom", new FluidBlock(CUSTOM, AbstractBlock.Settings.copy(Blocks.WATER)) {
	});
}
