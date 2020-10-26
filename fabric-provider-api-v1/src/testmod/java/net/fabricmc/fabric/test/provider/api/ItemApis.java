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

package net.fabricmc.fabric.test.provider.api;

import static net.fabricmc.fabric.test.provider.FabricProviderTest.MOD_ID;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.provider.v1.ContextKey;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookupRegistry;

public class ItemApis {
	public static final Identifier INSERTABLE_ID = new Identifier(MOD_ID, "item_insertable");
	public static final Identifier EXTRACTABLE_ID = new Identifier(MOD_ID, "item_extractable");
	public static final ContextKey<@NotNull Direction> SIDED = ContextKey.of(Direction.class, new Identifier("fabric", "sided"));
	public static final BlockApiLookup<ItemInsertable, @NotNull Direction> INSERTABLE = BlockApiLookupRegistry.getLookup(INSERTABLE_ID, SIDED);
	public static final BlockApiLookup<ItemExtractable, @NotNull Direction> EXTRACTABLE = BlockApiLookupRegistry.getLookup(EXTRACTABLE_ID, SIDED);
}
