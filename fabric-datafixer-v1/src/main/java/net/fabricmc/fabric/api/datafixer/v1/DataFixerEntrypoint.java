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

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

/**
 * Represents an Entrypoint used to register Entities, BlockEntities and TypeReferences for use in DataFixers.
 *
 * <p>Why this instead of just registering the TypeReferences and (Block)Entities straight onto your Schemas? The main issues is one mod cannot access another mod's DataFixer's TypeReference.
 * So you must register that reference to the DataFixers you wish to be able to fix that type.
 */
public interface DataFixerEntrypoint {
	/**
	 * Registers custom entities for use in DataFixers.
	 *
	 * @param schema
	 * @param entityMap
	 * @return The inputted entityMap.
	 */
	void registerEntities(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap);

	/**
	 * Registers custom block entities for use in DataFixers.
	 *
	 * @param schema         The schema to register the Block Entity in.
	 * @param blockEntityMap The map of block entity type templates.
	 * @return The inputted blockEntityMap.
	 */
	void registerBlockEntities(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap);

	/**
	 * Registers Type References for use in DataFixers.
	 *
	 * @param schema           The schema to register
	 * @param entityTypes      All the TypeTemplates of entities in the Schema.
	 * @param blockEntityTypes All the TypeTemplates of block entities in the Schema.
	 */
	void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes);
}
