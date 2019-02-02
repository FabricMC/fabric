/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.events;

import net.fabricmc.fabric.api.loot.FabricLootSupplier;
import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

/**
 * An event handler that is called when loot tables are loaded.
 * Use {@link #REGISTRY} to register instances.
 */
@FunctionalInterface
public interface LootTableLoadingCallback extends BiConsumer<Identifier, FabricLootSupplier> {
	final HandlerRegistry<LootTableLoadingCallback> REGISTRY = new HandlerArray<>(LootTableLoadingCallback.class);
}
