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

package net.fabricmc.fabric.test.item.client;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.FabricItemUpdateAnimationHandlers;
import net.fabricmc.fabric.test.item.client.item.UpdatingItem;

@Environment(EnvType.CLIENT)
public class UpdateAnimationTests implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Item patchedItem = Registry.register(Registry.ITEM, new Identifier("fabrictest", "patched"), new UpdatingItem(new Item.Settings()));
		Item originalItem = Registry.register(Registry.ITEM, new Identifier("fabrictest", "original"), new UpdatingItem(new Item.Settings()));
		Item defaultItem = Registry.register(Registry.ITEM, new Identifier("fabrictest", "default"), new Item(new Item.Settings()));

		FabricItemUpdateAnimationHandlers.register(patchedItem, (original, result) -> false);
		FabricItemUpdateAnimationHandlers.register(defaultItem, (original, result) -> true);
	}
}
