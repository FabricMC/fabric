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

package net.fabricmc.fabric.test.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.enchantment.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.api.ModInitializer;

public class EnchantmentTestMod implements ModInitializer {
	public static final Enchantment DIRTY = Registry.register(Registry.ENCHANTMENT, new Identifier("fabric-enchantment-api-v1-testmod", "dirty"), new DirtyEnchantment());

	// Note to users of this testmod, enchantment capabilities in creative are weird, any enchantment
	// can be added to any item using an anvil when in creative. This is not the case in survival mode,
	// so make sure whenever you are testing your enchantments you're in survival.

	@Override
	public void onInitialize() {
		EnchantmentEvents.registerAll((enchantment, stack) -> {
			if (enchantment == Enchantments.FLAME && stack.getItem() == Items.DIAMOND_SHOVEL) {
				return TriState.TRUE;
			} else {
				return TriState.DEFAULT;
			}
		});
	}
}
