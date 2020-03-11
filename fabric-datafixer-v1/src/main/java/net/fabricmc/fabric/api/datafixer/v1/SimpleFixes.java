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

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.impl.datafixer.FabricSimpleFixes;

/**
 * This class contains several common data fixes that a mod could use to fix some simple data types.
 *
 * <p>You must have a {@link DataFixerBuilder} and {@link Schema} in order to use these data fixes.
 */
public interface SimpleFixes {
	/**
	 * Gets the instance of the {@link SimpleFixes}.
	 */
	SimpleFixes INSTANCE = FabricSimpleFixes.INSTANCE;

	/**
	 * A basic DataFix for changing block names.
	 * To use this, first create a DataFixerBuilder and register a schema. Next you would invoke this method, and fill in the parameters below:
	 *
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix. This has no effect on the fixing process and is used as a description of what the fix does.
	 * @param oldId The old name of the block to fix
	 * @param newId The name to change the block to if fixed.
	 * @param schema The Schema to apply this fix to.
	 */
	void addBlockRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema);

	/**
	 * A basic DataFix for changing item names.
	 *
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix. This has no effect on the fixing process and is used as a description of what the fix does.
	 * @param oldId The old name of the item to fix
	 * @param newId The name to change the item to if fixed.
	 * @param schema The Schema to apply this fix to.
	 */
	void addItemRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema);

	/**
	 * A basic DataFix for changing biome names.
	 *
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix. This has no effect on the fixing process and is used as a description of what the fix does.
	 * @param changes A map containing all the changes in biome name, where the key is the old biome name and the value is the new biome name.
	 * @param schema The Schema to add this fix to.
	 */
	void addBiomeRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema);
}
