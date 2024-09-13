/*
 * Copyright (c) 2024 FabricMC
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

package net.fabricmc.fabric;

public class FabricDev {
	public static final boolean ZERO_WEIGHT_WARNING = getProperty("zeroWeightWarning", true);
	public static final boolean LOG_MISSING_TRANSLATIONS = getProperty("logMissingTranslations", true);
	public static final boolean LOG_CONVENTION_ISSUES = getProperty("logConventionIssues", true);
	public static final boolean ALWAYS_AGREE_TO_EULA = getProperty("alwaysAgreeToEula", false);
	public static final boolean REGISTER_DEBUG_COMMANDS = getProperty("registerDebugCommands", true);
	public static final boolean ENABLE_COMMAND_EXCEPTION_LOGGING = getProperty("enableCommandExceptionLogging", true);
	public static final boolean ENABLE_COMMAND_ARGUMENT_LOGGING = getProperty("enableCommandArgumentLogging", true);
	public static final boolean THROW_ON_INVALID_BLOCK_BOXES = getProperty("throwOnInvalidBlockBoxes", true);
	public static final boolean ENABLE_UNPRIMED_HEIGHTMAP_LOGGING = getProperty("enableUnprimedHeightmapLogging", true);
	public static final boolean ENABLE_SUPPLIER_AND_RUNNABLE_DEBUGGING = getProperty("enableSupplierAndRunnableDebugging", false);
	public static final boolean ENABLE_EXCEPTION_IDE_PAUSING = getProperty("enableExceptionIdePausing", true);

	private static boolean getProperty(String name, boolean defaultValue) {
		try {
			return "true".equalsIgnoreCase(System.getProperty("fabric.dev" + name));
		} catch (Throwable e) {
			return defaultValue;
		}
	}
}
