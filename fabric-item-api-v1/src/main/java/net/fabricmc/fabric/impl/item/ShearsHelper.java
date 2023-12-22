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

package net.fabricmc.fabric.impl.item;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.TagRegistration;

public final class ShearsHelper {
	public static final TagKey<Item> FABRIC_SHEARS = TagRegistration.ITEM_TAG_REGISTRATION.registerFabric("shears");
	public static final List<RegistryEntry<Item>> SHEARS;

	public static boolean isShears(ItemStack stack) {
		return stack.isIn(FABRIC_SHEARS) || stack.getItem() instanceof ShearsItem;
	}

	private ShearsHelper() {
	}

	static {
		ImmutableList.Builder<RegistryEntry<Item>> builder = new ImmutableList.Builder<>();

		for (Item item : Registries.ITEM) {
			@SuppressWarnings("deprecation")
			RegistryEntry<Item> entry = item.getRegistryEntry();

			if (entry.isIn(FABRIC_SHEARS) || item instanceof ShearsItem) {
				builder.add(entry);
			}
		}

		SHEARS = builder.build();
	}
}
