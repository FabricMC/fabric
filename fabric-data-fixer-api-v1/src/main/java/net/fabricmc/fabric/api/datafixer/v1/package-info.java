/*
 * Copyright (c) 2016-2022 FabricMC
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
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

/**
 * <h2>Custom DataFixerUpper API</h2>
 *
 * <p>This API lets you register a {@code DataFixer} for your own mod, letting you use Minecraft's built-in
 * "old save compatibility" system without bodges!
 *
 * <p>Here is an example simple use of this API:
 * <pre><code>
 * // the latest version of the mod's data
 * // this should match the version of the last schema added!
 * // note that the default data version is 0, meaning that you can upgrade
 * //  from a version that did not have a fixer
 * //  (by registering a schema for upgrading from version 0 to version 1)
 * public static final int CURRENT_DATA_VERSION = 1;
 *
 * public static void initialize(ModContainer mod) {
 *     // create a builder
 *     var builder = new FabricDataFixerBuilder(CURRENT_DATA_VERSION);
 *     // add the "base" version 0 schema
 *     builder.addSchema(0, FabricDataFixes.BASE_SCHEMA);
 *     // add a schema for upgrading from version 0 to version 1
 *     Schema schemaV1 = builder.addSchema(1, IdentifierNormalizingSchema::new)
 *     // add fixes to the schema - for example, an item rename (identifier change)
 *     SimpleFixes.addItemRenameFix(builder, "Rename cool_item to awesome_item",
 *         new Identifier("mymod", "cool_item"),
 *         new Identifier("mymod", "awesome_item"),
 *         schemaV1);
 *
 *     // register the fixer!
 *     // this will create either an unoptimized fixer or an optimized fixer,
 *     //  depending on the game configuration
 *     FabricDataFixes.buildAndRegisterFixer(mod, builder);
 * }
 * </code></pre>
 *
 * @see net.fabricmc.fabric.api.datafixer.v1.FabricDataFixes
 * @see net.fabricmc.fabric.api.datafixer.v1.SimpleFixes
 * @see net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerBuilder
 */

package net.fabricmc.fabric.api.datafixer.v1;
