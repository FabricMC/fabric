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

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;
import net.fabricmc.fabric.api.item.v1.CustomItemPredicateRegistry;

public enum ShearsItemPredicate implements CustomItemPredicate {
	/**
	 * Allows Fabric API to replace any occurrences of {@link net.minecraft.item.Items#SHEARS minecraft:shears} with a custom predicate for stack-aware shears. This is the default if {@link #DENY} is not specified.
	 *
	 * @see net.fabricmc.fabric.api.item.v1.FabricItem#isShears
	 */
	ALLOW,
	/**
	 * Disallows Fabric API to replace any occurrences of {@link net.minecraft.item.Items#SHEARS minecraft:shears} with a custom predicate for stack-aware shears.
	 *
	 * @see net.fabricmc.fabric.api.item.v1.FabricItem#isShears
	 */
	DENY;

	public static final Codec<ShearsItemPredicate> CODEC = Codec.BOOL.comapFlatMap(bool -> DataResult.success(bool ? ALLOW : DENY), predicate -> predicate == ALLOW);

	@Override
	public Codec<? extends CustomItemPredicate> getCodec() {
		return CODEC;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return this == DENY || itemStack.isShears(); // if it is DENY, nothing will happen
	}

	static {
		CustomItemPredicateRegistry.register(new Identifier("fabric", "allow_shears"), CODEC);
	}
}
