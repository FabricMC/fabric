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

package net.fabricmc.fabric.impl.brewing;

import net.fabricmc.fabric.api.brewing.PotionTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PotionTypeRegistryImpl implements PotionTypeRegistry {
	private final Map<Identifier, Item> typeItems = new HashMap<>();

	public PotionTypeRegistryImpl() {
		registerPotionType("normal", Items.POTION);
		registerPotionType("splash", Items.SPLASH_POTION);
		registerPotionType("lingering", Items.LINGERING_POTION);
	}

	public Item getItem(Identifier id) { return typeItems.getOrDefault(id, Items.POTION); }
	public void registerPotionType(String id, Item item) { registerPotionType(new Identifier(id.toLowerCase()), item);}
	public void registerPotionType(Identifier id, Item item) { typeItems.put(id, item); }
}
