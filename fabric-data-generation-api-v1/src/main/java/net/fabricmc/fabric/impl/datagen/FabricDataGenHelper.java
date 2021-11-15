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

package net.fabricmc.fabric.impl.datagen;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public class FabricDataGenHelper {
	public static final boolean ENABLED = System.getProperty("fabric-api.datagen") != null;
	public static final String OUTPUT_DIR = System.getProperty("fabric-api.datagen.output-dir");

	private static final String ENTRYPOINT_KEY = "fabric-datagen";

	public static void run() throws IOException {
		Path outputDir = Paths.get(Objects.requireNonNull(OUTPUT_DIR, "No output dir provided with the 'fabric-api.datagen.output-dir' property"));

		List<EntrypointContainer<DataGeneratorEntrypoint>> dataGeneratorInitializers = FabricLoader.getInstance()
				.getEntrypointContainers(ENTRYPOINT_KEY, DataGeneratorEntrypoint.class);

		for (EntrypointContainer<DataGeneratorEntrypoint> entrypointContainer : dataGeneratorInitializers) {
			FabricDataGenerator dataGenerator = new FabricDataGenerator(outputDir, entrypointContainer.getProvider());
			entrypointContainer.getEntrypoint().onInitializeDataGenerator(dataGenerator);
			dataGenerator.run();
		}
	}
}
