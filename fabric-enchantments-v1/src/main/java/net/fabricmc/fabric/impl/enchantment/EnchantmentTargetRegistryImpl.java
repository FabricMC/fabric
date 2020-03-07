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

package net.fabricmc.fabric.impl.enchantment;

import net.fabricmc.fabric.api.enchantment.v1.EnchantmentTargetRegistry;
import net.fabricmc.fabric.api.enchantment.v1.FabricEnchantmentTarget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class EnchantmentTargetRegistryImpl implements EnchantmentTargetRegistry {
	public static final EnchantmentTargetRegistryImpl INSTANCE = new EnchantmentTargetRegistryImpl();

	private final Collection<FabricEnchantmentTarget> enchantmentTargets = new HashSet<>();

	@Override
	public void register(FabricEnchantmentTarget enchantmentTarget) {
		enchantmentTargets.add(enchantmentTarget);
	}

	@Override
	public Iterator<FabricEnchantmentTarget> getIterator() {
		return enchantmentTargets.iterator();
	}
}
