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

package net.fabricmc.fabric.impl.datafixer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.schemas.Schema;

import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.datafixers.Schemas;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents Minecraft's Built in DataFixer.
 */
public class MCDFU {

	private static final Logger LOGGER = LogManager.getLogger("Fabric-DataFixer");

	private static final int LATEST_SCHEMA_VERSION;
	
	static {
		// Logic to automatically resolve the Latest schema version
		DataFixerUpper mcDFU = (DataFixerUpper) Schemas.getFixer();
		
		try {
			final Field fixerVersions = DataFixerUpper.class.getDeclaredField("fixerVersions");
			fixerVersions.setAccessible(true);
			IntSortedSet fixerVersionsReflected = (IntSortedSet) fixerVersions.get(mcDFU);
			LATEST_SCHEMA_VERSION = fixerVersionsReflected.lastInt();
		} catch (ReflectiveOperationException e) {
			CrashReport report = CrashReport.create(e, "Exception while grabbing Vanilla Schema");
			throw new CrashException(report);
		}
		
		LOGGER.info("[Fabric-DataFixer] Started with MC-DFU version: " + LATEST_SCHEMA_VERSION);
	}
	
	public static final BiFunction<Integer, Schema, Schema> MC_TYPE_REFS = (version, parent) -> Schemas.getFixer().getSchema(LATEST_SCHEMA_VERSION);
}
