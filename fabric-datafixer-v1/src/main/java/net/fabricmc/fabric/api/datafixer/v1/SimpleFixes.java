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
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.impl.datafixer.FabricSimpleFixes;
import net.minecraft.nbt.Tag;

/**
 * This class contains several common datafixes modders would use.
 *
 * <p>
 *
 * </p>
 */
public interface SimpleFixes {
	public static final SimpleFixes INSTANCE = FabricSimpleFixes.INSTANCE;

	/**
	 * A basic DataFix for changing block names.
	 *
	 * To use this, first create a DataFixerBuilder and register a schema. Next you would invoke this method, and fill in the arguments to
	 *
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix (this has no effect on actual process)
	 * @param oldId The old name of the block to fix
	 * @param newId The name to change the block to if fixed.
	 * @param schema The Schema to apply this fix to.
	 */
	public abstract void addBlockRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema);

	/**
	 * A basic DataFix for changing item names.
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix (this has no effect on actual process)
	 * @param oldId The old name of the item to fix
	 * @param newId The name to change the item to if fixed.
	 * @param schema The Schema to apply this fix to.
	 */
	public abstract void addItemRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema);

	/**
	 * A basic DataFix for changing biome names.
	 * 
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix (this has no effect on actual process)
	 * @param changes A map containing all the changes in biome name, where the key is the old biome name and the value is the new biome name.
	 * @param schema The Schema to add this fix to.
	 */
	public abstract void addBiomeRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema);

	/**
	 * A basic DataFix for changing blockentity names -- Untested as of now
	 * 
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix (this has no effect on actual process)
	 * @param oldId the original name of the BlockEntity
	 * @param newId the new desired name of the BlockEntity being renamed.
	 * @param schema The Schema to add this fix to.
	 */
	public abstract void addBlockEntityRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema);

	/**
	 * A basic DataFix for changing entity names.
	 * <p>
	 * Note this does not rename entity spawn eggs and you should use {@link #addItemRenameFix(DataFixerBuilder, String, String, String, Schema)} to rename the spawn egg item.
	 * </p>
	 * 
	 * @param builder The builder to add this fix to.
	 * @param name The name of the datafix (this has no effect on actual process).
	 * @param oldId the original name of the Entity.
	 * @param newId the new desired name of the Entity being renamed.
	 * @param schema The Schema to add this fix to.
	 */
	public abstract void addEntityRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema);

	/**
	 * A basic DataFix for transforming an entity.
	 * 
	 * @param builder The builder to add this fix to.
	 * @param name The name of the DataFix (this has no effect on actual process)
	 * @param transformation The transformation to apply to the input entity.
	 * @param schema The Schema to add this fix to.
	 */
	public abstract void addEntityTransformFix(DataFixerBuilder builder, String name, EntityTransformation transformation, Schema schema);

	/**
	 * Represents an entity transformation function for a DataFix.
	 */
	@FunctionalInterface
	public interface EntityTransformation {

		/**
		 * Transforms an entity.
		 * 
		 * @param entityName The input entity's name.
		 * @param dynamic The Dynamic object representing the entity.
		 * @return A Pair which contains the entity's new name and the dynamic representing the entity.
		 */
		Pair<String, Dynamic<Tag>> transform(String entityName, Dynamic<Tag> dynamic);
	}

}
