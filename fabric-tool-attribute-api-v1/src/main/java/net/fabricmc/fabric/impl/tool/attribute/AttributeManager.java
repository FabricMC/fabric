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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.attribute.EntityAttribute;
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
	public static Multimap<EntityAttribute, EntityAttributeModifier> mergeAttributes(Multimap<EntityAttribute, EntityAttributeModifier> left, Multimap<EntityAttribute, EntityAttributeModifier> right) {
		if (left.isEmpty()) return right;
		if (right.isEmpty()) return left;
		Multimap<EntityAttribute, EntityAttributeModifier> ret = HashMultimap.create();
		Set<EntityAttribute> allKeys = new HashSet<>();
		allKeys.addAll(left.keySet());
		allKeys.addAll(right.keySet());

		for (EntityAttribute key : allKeys) {
			Collection<EntityAttributeModifier> leftModifiers = left.get(key);
			Collection<EntityAttributeModifier> rightModifiers = right.get(key);

			if (leftModifiers.isEmpty()) {
				ret.putAll(key, rightModifiers);
			} else if (rightModifiers.isEmpty()) {
				ret.putAll(key, leftModifiers);
			} else {
				Collection<EntityAttributeModifier> modifiers = new ArrayList<>(leftModifiers.size() + rightModifiers.size());
				modifiers.addAll(leftModifiers);
				modifiers.addAll(rightModifiers);

				EntityAttributeModifier lastAddMod = null;
				EntityAttributeModifier lastMultBaseMod = null;
				EntityAttributeModifier lastMultTotalMod = null;
				int addCount = 0;
				int multBaseCount = 0;
				int multTotalCount = 0;
				double add = 0;
				double multBase = 1;
				double multTotal = 1;

				for (EntityAttributeModifier mod : modifiers) {
					double amount = mod.getValue();
					if (amount == 0) continue;

					switch (mod.getOperation()) {
					case ADDITION:
						lastAddMod = mod;
						addCount++;
						add += amount;
						break;
					case MULTIPLY_BASE:
						lastMultBaseMod = mod;
						multBaseCount++;
						multBase += amount;
						break;
					case MULTIPLY_TOTAL:
						lastMultTotalMod = mod;
						multTotalCount++;
						multTotal *= 1 + amount;
						break;
					default:
						break;
					}
				}

				if (addCount == 1) {
					ret.put(key, lastAddMod);
				} else if (addCount > 0) {
					ret.put(key, new EntityAttributeModifier("merged add", add, EntityAttributeModifier.Operation.ADDITION));
				}

				if (multBaseCount == 1) {
					ret.put(key, lastMultBaseMod);
				} else if (multBaseCount > 0) {
					ret.put(key, new EntityAttributeModifier("merged multiply-base", multBase, EntityAttributeModifier.Operation.MULTIPLY_BASE));
				}

				if (multTotalCount == 1) {
					ret.put(key, lastMultTotalMod);
				} else if (multTotalCount > 0) {
					ret.put(key, new EntityAttributeModifier("merged multiply-total", multTotal, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
				}
			}
		}

		return ret;
	}
}
