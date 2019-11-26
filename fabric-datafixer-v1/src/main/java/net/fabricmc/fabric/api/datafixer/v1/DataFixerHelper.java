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

package net.fabricmc.fabric.api.datafixer.v1;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.types.Type;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;

/**
 * This registers and creates DataFixers for the game, along with some other util methods.
 *
 * <p><b>Please take extreme caution when using these tools as DataFixers directly interface with the world saves and may corrupt world saves.</b>
 *
 * <p><h2>How to make a DataFixer</h2>
 *
 *     <p>You first need to create a DataFixerBuilder using {@link com.mojang.datafixers.DataFixerBuilder#DataFixerBuilder(int)}.
 *
 *     <p><code>DataFixerBuilder builder = new DataFixerBuilder(DATAFIXER_VERSION); // This should be the latest Schema version of your DataFixer.</code>
 *
 *     <p>Then you would register the first Schema, this is required or all {@link net.minecraft.datafixers.TypeReferences} will fail when you attempt to fix any data.
 *     Use {@link com.mojang.datafixers.DataFixerBuilder#addSchema(int, BiFunction)} to register {@link FabricSchemas#FABRIC_SCHEMA}.
 *     <code>builder.addSchema(0, FabricSchemas.FABRIC_SCHEMA); // Reccomended to use version 0 for the base Schema.</code>
 *
 *     <p>Next you would register another Schema (version 1, 2, 60549) anything works, just make sure you keep it in sequential order.
 *
 *     <p><code>Schema version1 = builder.addSchema(1, Schema::new);</code>
 *
 *     <p>Next your DataFixers would be registered onto the Schemas. It is advised to use {@link SimpleFixes} as that will cover most cases. Though you can create your own fixes which can be a complex process. How to use the fixes is shown in {@link SimpleFixes}
 *
 *     <p>Finally you need to build and register your DataFixer for use.
 *         First build the DataFixer using {@link com.mojang.datafixers.DataFixerBuilder#build(Executor)}. The executor should be {@link Util#getServerWorkerExecutor()}.
 *         Finally register the DataFixer using {@link DataFixerHelper#registerFixer(String, int, DataFixer)} so that the DataFixer is invoked when the game normally fixes any data.
 *
 * <p>If you need more help, see the wiki article here: https://fabricmc.net/wiki/tutorial:datafixer or visit the Fabric Discord server.
 */
public interface DataFixerHelper {
	/**
	 * Gets the instance of the {@link DataFixerHelper}.
	 */
	DataFixerHelper INSTANCE = FabricDataFixerImpl.INSTANCE;

	/**
	 * Registers a DataFixer.
	 *
	 * @param modid The modid of the mod registering this DataFixer
	 * @param runtimeDataVersion the current dataversion of the mod being ran.
	 * @param datafixer The DataFixer to register
	 * @return The inputted DataFixer
	 */
	DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer);

	/**
	 * Gets the DataFixer registered under a mod.
	 *
	 * @param modid The Modid which the DataFixer was registered under.
	 * @return An optional, which may contain a DataFixer if a mod has registered a DataFixer.
	 * @throws IllegalArgumentException if the mod with the following modid does not have a DataFixer.
	 */
	DataFixer getDataFixer(String modid);

	/**
	 * Retrieves the DataVersion registered under a modid.
	 *
	 * @param compoundTag The CompoundTag to check
	 * @param modid The modid to check.
	 * @return The DataVersion stored for the mod or 0 if no DataVersion or mod is present.
	 */
	int getModDataVersion(CompoundTag compoundTag, String modid);

	/**
	 * Gets a Type for use in creating a (Block)Entity.
	 * @param dataFixer The DataFixer to get the Type from.
	 * @param schemaVersion The Schema version where the type is registered
	 * @param typeReference The TypeReference of the Type.
	 * @param identifier The identifier of the Type.
	 * @return The Type.
	 * @throws IllegalArgumentException If the Type does not exist, or has not been registered.
	 * @throws IllegalArgumentException If the Schema version does not exist.
	 */
	Type<?> getChoiceType(DataFixer dataFixer, int schemaVersion, DSL.TypeReference typeReference, String identifier);

	/**
	 * Checks if Fabric is allowing any more DataFixers to be registered.
	 * @return true if registration is locked, otherwise false.
	 */
	boolean isLocked();
}
