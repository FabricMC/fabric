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
import net.minecraft.util.Identifier;

/**
 * Allows for adding custom {@linkplain ItemPredicate item predicate}s, used in advancements and loot tables.
 *
 * <p>Custom predicates can be added with its registered id as the key.
 * <b>Example</b>, in an advancement criterion: <pre>{@code
 * "trigger": "minecraft:using_item",
 * "conditions": {
 *   "item": {
 *     // vanilla values
 *     "durability": { "max": 20 },
 *     "count": { "min": 3 },
 *
 *     // custom values
 *     "mymod:int": 3,
 *     "mymod:object": {
 *       "something": true
 *     }
 *   }
 * }
 * }</pre>
 */
public interface CustomItemPredicate extends Predicate<ItemStack> {
	/**
	 * Returns the codec for this predicate.
	 *
	 * <p>The codec needs to also be {@linkplain CustomItemPredicateRegistry#register(Identifier, Codec) registered}.
	 */
	Codec<? extends CustomItemPredicate> getCodec();
}
