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

package net.fabricmc.fabric.impl;

import java.util.function.Supplier;

import com.mojang.brigadier.ParseResults;

import net.minecraft.Bootstrap;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.util.collection.Weight;
import net.minecraft.world.Heightmap;

import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("JavadocReference")
public class FabricDevProperties {
	/**
	 * Logs an error when a weight is set to zero</br>
	 * Property: <code>fabric.dev.zeroWeightWarning</code></br>
	 * Default value: true</br>
	 * {@link Weight#validate(int)}
	 */
	public static final boolean ZERO_WEIGHT_WARNING = getProperty("zeroWeightWarning", true);

	/**
	 * Logs an error when a translation is missing</br>
	 * Property: <code>fabric.dev.logMissingTranslations</code></br>
	 * Default value: true</br>
	 * {@link Bootstrap#logMissing()}
	 */
	public static final boolean LOG_MISSING_TRANSLATIONS = getProperty("logMissingTranslations", true);

	/**
	 * Logs an error if Block classes don't end with Block and if Item classes don't end with Item</br>
	 * Property: <code>fabric.dev.logConventionIssues</code></br>
	 * Default value: true</br>
	 * {@link net.minecraft.block.Block#Block} and {@link net.minecraft.item.Item#Item}
	 */
	public static final boolean LOG_CONVENTION_ISSUES = getProperty("logConventionIssues", true);

	/**
	 * Skips creating the eula.txt file and always agrees to the EULA</br>
	 * <strong>Note: By enabling this, you declare that you have agreed to the EULA.</strong></br>
	 * Property: <code>fabric.dev.alwaysAgreeToEula</code></br>
	 * Default value: false</br>
	 * {@link net.minecraft.server.dedicated.EulaReader#EulaReader} and {@link EulaReader#createEulaFile()}
	 */
	public static final boolean ALWAYS_AGREE_TO_EULA = getProperty("alwaysAgreeToEula", false);

	/**
	 * Registers Minecraft's debug commands
	 * (TestCommand, RaidCommand, DebugPathCommand, DebugMobSpawningCommand,
	 * WardenSpawnTrackerCommand, SpawnArmorTrimsCommand, ServerPackCommand),
	 * and if on the server DebugConfigCommand</br>
	 * Property: <code>fabric.dev.registerDebugCommands</code></br>
	 * Default value: true</br>
	 * {@link CommandManager#CommandManager}
	 */
	public static final boolean REGISTER_DEBUG_COMMANDS = getProperty("registerDebugCommands", true);

	/**
	 * Logs an error if a command threw an exception</br>
	 * Property: <code>fabric.dev.enableCommandExceptionLogging</code></br>
	 * Default value: true</br>
	 * {@link CommandManager#execute(ParseResults, String)}
	 */
	public static final boolean ENABLE_COMMAND_EXCEPTION_LOGGING = getProperty("enableCommandExceptionLogging", true);

	/**
	 * Logs an error regarding argument ambiguity and throws an exception if an argument type is not registered</br>
	 * Property: <code>fabric.dev.enableCommandArgumentLogging</code></br>
	 * Default value: true</br>
	 * {@link CommandManager#checkMissing()}
	 */
	public static final boolean ENABLE_COMMAND_ARGUMENT_LOGGING = getProperty("enableCommandArgumentLogging", true);

	/**
	 * Throw's an exception if a bounding box is invalid</br>
	 * Property: <code>fabric.dev.throwOnInvalidBlockBoxes</code></br>
	 * Default value: true</br>
	 * {@link net.minecraft.util.math.BlockBox#BlockBox(int, int, int, int, int, int)}
	 */
	public static final boolean THROW_ON_INVALID_BLOCK_BOXES = getProperty("throwOnInvalidBlockBoxes", true);

	/**
	 * Logs an error if the heightmap is null</br>
	 * Property: <code>fabric.dev.enableUnprimedHeightmapLogging</code></br>
	 * Default value: true</br>
	 * {@link net.minecraft.world.chunk.Chunk#sampleHeightmap(Heightmap.Type, int, int)}
	 */
	public static final boolean ENABLE_UNPRIMED_HEIGHTMAP_LOGGING = getProperty("enableUnprimedHeightmapLogging", true);

	/**
	 * Set's the current thread's name to the activeThreadName if debugRunnable or debugSupplier is called</br>
	 * Property: <code>fabric.dev.enableSupplierAndRunnableDebugging</code></br>
	 * Default value: false</br>
	 * {@link net.minecraft.util.Util#debugRunnable(String, Runnable)} and {@link net.minecraft.util.Util#debugSupplier(String, Supplier)}
	 */
	public static final boolean ENABLE_SUPPLIER_AND_RUNNABLE_DEBUGGING = getProperty("enableSupplierAndRunnableDebugging", false);

	/**
	 * Invokes a method in which you should have a breakpoint to debug errors
	 * thrown with Util#error and exceptions thrown with Util#throwOrPause</br>
	 * Property: <code>fabric.dev.enableExceptionIdePausing</code></br>
	 * Default value: true</br>
	 * {@link net.minecraft.util.Util#error(String)}, {@link net.minecraft.util.Util#error(String, Throwable)}
	 * and {@link net.minecraft.util.Util#throwOrPause(Throwable)}
	 */
	public static final boolean ENABLE_EXCEPTION_IDE_PAUSING = getProperty("enableExceptionIdePausing", true);

	private static final boolean IS_DEVELOPMENT_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();

	private static boolean getProperty(String name, boolean defaultValue) {
		String propertyValue = System.getProperty("fabric.dev" + name);
		if (propertyValue == null || propertyValue.isEmpty())
			return IS_DEVELOPMENT_ENV && defaultValue;
		return "true".equalsIgnoreCase(propertyValue);
	}
}
