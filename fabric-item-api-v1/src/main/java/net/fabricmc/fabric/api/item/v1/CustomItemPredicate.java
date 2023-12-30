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

package net.fabricmc.fabric.api.item.v1;

import java.util.function.Predicate;

import com.mojang.serialization.Codec;

import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

/**
 * Allows for adding custom {@linkplain ItemPredicate item predicate}s, used in advancements and loot tables.
 *
 * <p><b>JSON format</b>: along with the vanilla object ({@code {}}) syntax,
 * Fabric also adds an alternate syntax using a list ({@code []}) that will allow for custom predicates.
 * The list contains objects whose format follows the codec of the custom predicate,
 * along with {@code fabric:type} value denoting the predicate id, as specified when registering it.
 *
 * <p>Vanilla predicate can be used with the type {@code minecraft:default}.
 *
 * <p><b>Example</b>, in an advancement criterion: <pre>{@code
 * "trigger": "minecraft:using_item",
 * "conditions": {
 *   "item": [{
 *     "fabric:type": "mymod:tier",
 *     "tier": 1
 *   }, {
 *     "fabric:type": "minecraft:default",
 *     "durability": { "max": 20 },
 *     "count": { "min": 3 }
 *   }]
 * }
 * }</pre>
 */
public interface CustomItemPredicate extends Predicate<ItemStack> {
	RegistryKey<Registry<Codec<? extends CustomItemPredicate>>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier("fabric", "custom_item_predicates"));
	Registry<Codec<? extends CustomItemPredicate>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

	/**
	 * Returns the codec for this predicate.
	 *
	 * <p>The codec needs to also be registered into {@link #REGISTRY}.
	 */
	Codec<? extends CustomItemPredicate> getCodec();
}
