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

import java.util.function.BiFunction;

import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.impl.datafixer.FabricSchema;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;


/**
 * This class allows quick access to common Schema types used in the game.
 * 
 */
public class FabricSchemas {
	/**
	 * Fabric Schema Type. This is required for all custom DataFixers or fixing will fail. This should be registered under version 0 within your DataFixer.
	 */
	public static final BiFunction<Integer, Schema, Schema> FABRIC_SCHEMA = FabricSchema.MC_TYPE_REFS;
	/**
	 * Identifier Normalize Schema. 
	 */
	public static final BiFunction<Integer, Schema, Schema> IDENTIFIER_NORMALIZE_SCHEMA = SchemaIdentifierNormalize::new;

	/**
	 * Empty Schema. Nothing special just an empty Schema.
	 * @see Schema
	 */
	public static final BiFunction<Integer, Schema, Schema> EMPTY_SCHEMA = Schema::new;
}
