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

/**
 * <h1>The Api Lookup, version 1.</h1>
 *
 * <p><h2>Definitions and purpose</h2>
 * <ul>
 *     <li>What we call an <i>Api</i> is any object that can be offered or queried, possibly by different mods, to be used in an agreed-upon manner.
 *     In this module, such objects are represented by the generic type {@code T}.</li>
 *     <li>This module allows flexible retrieving of such Apis from blocks in the world.</li>
 *     <li>It also provides building blocks for defining ways of retrieving Apis from other game objects.</li>
 * </ul>
 * </p>
 *
 * <p><h2>Retrieving Apis from blocks in the world</h2>
 * <ul>
 *     <li>A block query for an Api is an operation that takes a world, a block position, and additional context of type {@code T}, and uses that
 *     to retrieve an object of type {@code T}, or {@code null} if there was no such object. An instance of {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup BlockApiLookup&lt;T, C&gt;}
 *     provides a {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup#get get()} function that does exactly that. It also allows registering Apis for blocks,
 *     because for the query to work the Api must be registered first. Registration primarily happens through {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup#registerForBlocks registerForBlocks()}
 *     and {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup#registerForBlockEntities registerForBlockEntities()}.</li>
 *     <li>{@code BlockApiLookup} instances can be accessed through {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry#getLookup BlockApiLookupRegistry#getLookup()}.
 *     For optimal performance, it is better to store them in a {@code public static final} field instead of querying them multiple times.</li>
 *     <li>Speaking of performance, a lot of block Api queries can happen every tick, and {@code BlockApiLookup} may not be fast enough. In that case,
 *     {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache BlockApiCache&lt;T, C&gt;} can be used instead.</li>
 * </ul>
 * </p>
 *
 * <p><h2>Retrieving Apis from custom game objects</h2>
 * <ul>
 *     <li>To make a custom Api lookup implementation easier, this modules provide {@link net.fabricmc.fabric.api.lookup.v1.ApiProviderMap ApiProviderMap} to help with
 *     the fast handling of providers.</li>
 *     <li>This module also provides an {@link net.fabricmc.fabric.api.lookup.v1.ApiLookupMap ApiLookupMap} to ease the creation of a registry
 *     for a custom Api lookup.</li>
 * </ul>
 * </p>
 */
package net.fabricmc.fabric.api.lookup.v1;
