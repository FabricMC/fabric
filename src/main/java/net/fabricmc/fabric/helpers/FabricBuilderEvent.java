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

package net.fabricmc.fabric.helpers;

import net.fabricmc.fabric.util.HandlerList;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.function.BiConsumer;

public final class FabricBuilderEvent {
	public static final HandlerRegistry<BiConsumer<Block.Builder, Block>> BLOCK = new HandlerList<>();
	public static final HandlerRegistry<BiConsumer<Item.Builder, Item>> ITEM = new HandlerList<>();

	private FabricBuilderEvent() {

	}
}
