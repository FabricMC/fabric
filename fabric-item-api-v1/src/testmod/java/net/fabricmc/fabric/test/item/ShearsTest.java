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

package net.fabricmc.fabric.test.item;

import net.minecraft.item.Item;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class ShearsTest implements ModInitializer {
	public static final Item REAL_SHEARS = new ShearsItem(new FabricItemSettings().maxDamage(38)); // to show that ShearsItem will work
	public static final Item FAKE_SHEARS = new Item(new FabricItemSettings().maxDamage(38)); // to show that anything in fabric:shears will work

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "real_shears"), REAL_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "fake_shears"), FAKE_SHEARS);
	}
}
