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
 * <p>This API lets you register a {@code DataFixer} for your own mod, allowing mods to make
 * changes affecting world save without breaking compatibility!
 *
 * <p>Here is an example simple use of this API:
 * <pre>{@code
 * // the latest version of the mod's data
 * // this should match the version of the last schema added!
 * // note that the default data version is 0, meaning that you can upgrade
 * //  from a version that did not have a fixer
 * //  (by registering a schema for upgrading from version 0 to version 1)
 * public static final int CURRENT_DATA_VERSION = 1;
 *
 * public static void initialize(ModContainer mod) {
 *     // create a builder
 *     FabricDataFixerBuilder builder = new FabricDataFixerBuilder(CURRENT_DATA_VERSION);
 *     // add the "base" version 0 schema
 *     builder.addSchema(0, FabricDataFixes.BASE_SCHEMA);
 *     // add a schema for upgrading from version 0 to version 1
 *     Schema schemaV1 = builder.addSchema(1, IdentifierNormalizingSchema::new)
 *     // add fixes to the schema - for example, an item rename (identifier change)
 *     // multiple fixes can share the same schema
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
 * }</pre>
 *
 * <h3 id="data-version">Data version</h3>
 *
 * <p>A data fixer needs an integer "data version" to function. This is different from the
 * mod version given in {@code version} key of the {@code fabric.mod.json} file.
 *
 * <p>The default data version, used for versions without datafixers, is {@code 0}.
 * To add a data fixer, you must assign a positive integer as the current data version.
 * This is increased every time a new data fixer is added, unless the version is
 * already increased in the same release. Multiple mod versions can share the same
 * data version if no data fixes are needed between those.
 *
 * <p>Data versions do not have to be consecutive (incremented one by one). However,
 * it should never decrease. When making a significant change (such as forking or
 * updating to a new Minecraft release), it is usually recommended to have a "gap"
 * between the two data versions, to be used for data fixers before the significant
 * change.
 *
 * <p>There are three ways to specify the current data version:
 * <ul>
 *     <li>By passing it to {@link net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerBuilder}.</li>
 *     <li>By passing it to {@link
 *     net.fabricmc.fabric.api.datafixer.v1.FabricDataFixes#registerFixer(net.fabricmc.loader.api.ModContainer, int, com.mojang.datafixers.DataFixer)}
 *     or other overloads taking the current version.</li>
 *     <li>By setting it in the {@code fabric-data-fixer-api-v1:version} field of the {@code custom}
 *     object in the {@code fabric.mod.json} file, and using a method that uses the version.</li>
 * </ul>
 *
 * <p>The data version for a specific data fix is assigned when creating a schema using
 * the {@code addSchema} method. Multiple data fixes can share the same schema, if they
 * all provide fixes for the same data version.
 *
 * @see net.fabricmc.fabric.api.datafixer.v1.FabricDataFixes
 * @see net.fabricmc.fabric.api.datafixer.v1.SimpleFixes
 * @see net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerBuilder
 */

// From QSL.
package net.fabricmc.fabric.api.datafixer.v1;
