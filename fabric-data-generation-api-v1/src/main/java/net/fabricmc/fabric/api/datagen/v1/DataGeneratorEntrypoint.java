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

package net.fabricmc.fabric.api.datagen.v1;

/**
 * An entry point for data generation.
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with {@code fabric-datagen} key.</p>
 *
 * @see FabricDataGenerator
 */
@FunctionalInterface
public interface DataGeneratorEntrypoint {
	/**
	 * Register {@link net.minecraft.data.DataProvider} with the {@link FabricDataGenerator} during this entrypoint.
	 *
	 * @param fabricDataGenerator The {@link FabricDataGenerator} instance
	 */
	void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator);
}
