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

package net.fabricmc.fabric.api.gametest.v1;

import net.fabricmc.fabric.impl.client.gametest.FabricGameTestHelperImpl;

public interface FabricGameTestRegistry {
	/**
	 * Use in {@link net.minecraft.test.GameTest} structureName to use an empty 8x8 structure for the test.
	 */
	String EMPTY_STRUCTURE = "fabric-game-test-api-v1:empty";

	/**
	 * Register a class to be used as a test suite.
	 *
	 * @param testClass The test suite class
	 * @param modid The modid of the suite, used to determine the structure resource namespace
	 */
	static void register(Class<?> testClass, String modid) {
		FabricGameTestHelperImpl.register(testClass, modid);
	}
}
