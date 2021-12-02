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

package net.fabricmc.fabric.test.fluid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.test.fluid.block.MBlocks;
import net.fabricmc.fabric.test.fluid.core.ModCore;
import net.fabricmc.fabric.test.fluid.fluid.MFluids;
import net.fabricmc.fabric.test.fluid.item.MItems;

public class FabricFluidTestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		MFluids.load();
		MBlocks.load();
		MItems.load();

		ModCore.LOGGER.info("Loaded Fluid test mod main-side.");
	}
}
