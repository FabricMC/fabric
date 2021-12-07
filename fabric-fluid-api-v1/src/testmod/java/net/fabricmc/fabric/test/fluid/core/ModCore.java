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

package net.fabricmc.fabric.test.fluid.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.test.fluid.FabricFluidTestMod;

@SuppressWarnings("unused")
public class ModCore {
	public static final String ID = "fabric-fluid-api-v1-testmod";

	public static final Logger LOGGER = LogManager.getLogger(FabricFluidTestMod.class);

	public static boolean isBetween(@NotNull Vec3d init, @NotNull Vec3d end, @NotNull Vec3d pos) {
		return between(init.x, end.x, pos.x) && between(init.y, end.y, pos.y) && between(init.z, end.z, pos.z);
	}

	private static boolean between(double init, double end, double val) {
		return (val >= init && val <= end) || (val >= end && val <= init);
	}
}
