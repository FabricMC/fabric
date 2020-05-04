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

package net.fabricmc.fabric.impl.tool.attribute;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public final class AttributeManager {
	private AttributeManager() { }

	/**
	 * Merge two multimaps of EntityAttributeModifiers, combining their modifiers to prevent duplicate entries in a tooltip.
	 *
	 * @param left  The first of the two multimaps to merge.
	 * @param right The second of the two multimaps to merge.
	 * @return The merged form of the two.
	 */
	public static Multimap<String, EntityAttributeModifier> mergeAttributes(Multimap<String, EntityAttributeModifier> left, Multimap<String, EntityAttributeModifier> right) {
		if (left.isEmpty()) return right;
		if (right.isEmpty()) return left;
		Multimap<String, EntityAttributeModifier> ret = HashMultimap.create();
		Set<String> allKeys = new HashSet<>();
		allKeys.addAll(left.keySet());
		allKeys.addAll(right.keySet());

		for (String key : allKeys) {
			double add = 0;
			double multBase = 1;
			double multTotal = 1;
			Collection<EntityAttributeModifier> modifiers;

			if (left.containsKey(key)) {
				modifiers = left.get(key);
				if (right.containsKey(key)) modifiers.addAll(right.get(key));
			} else {
				//key *must* be in either left or right, so if it's not in left, it *must* be in right
				modifiers = right.get(key);
			}

			for (EntityAttributeModifier mod : modifiers) {
				switch (mod.getOperation()) {
				case ADDITION:
					add += mod.getAmount();
					break;
				case MULTIPLY_BASE:
					multBase += mod.getAmount();
					break;
				case MULTIPLY_TOTAL:
					multTotal *= 1 + mod.getAmount();
					break;
				default:
					break;
				}
			}

			ret.put(key, new EntityAttributeModifier(key, add, EntityAttributeModifier.Operation.ADDITION));
			ret.put(key, new EntityAttributeModifier(key, multBase, EntityAttributeModifier.Operation.MULTIPLY_BASE));
			ret.put(key, new EntityAttributeModifier(key, multTotal, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		}

		return ret;
	}
}
