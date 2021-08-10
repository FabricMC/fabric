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

import java.lang.reflect.Method;

import net.minecraft.test.TestContext;

import net.fabricmc.fabric.impl.gametest.FabricGameTestHelper;

/**
 * This interface can be optionally implemented on your test class.
 */
public interface FabricGameTest {
	/**
	 * Use in {@link net.minecraft.test.GameTest} structureName to use an empty 8x8 structure for the test.
	 */
	String EMPTY_STRUCTURE = "fabric-gametest-api-v1:empty";

	/**
	 * Override this method to implement custom logic to invoke the test method.
	 * This can be used to run code before or after each test.
	 * You can also pass in custom objects into the test method if desired.
	 * The structure will have been placed in the world before this method is invoked.
	 *
	 * @param context The vanilla test context
	 * @param method The test method to invoke
	 */
	default void invokeTestMethod(TestContext context, Method method) {
		FabricGameTestHelper.invokeTestMethod(context, method, this);
	}
}
