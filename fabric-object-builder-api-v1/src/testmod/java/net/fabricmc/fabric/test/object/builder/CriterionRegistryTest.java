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

package net.fabricmc.fabric.test.object.builder;

import com.google.gson.JsonObject;

import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public final class CriterionRegistryTest {
	public static void init() {
		CriterionRegistry.register(new CustomCriterion());
	}

	static class CustomCriterion extends ImpossibleCriterion {
		static final Identifier ID = ObjectBuilderTestConstants.id("custom");

		@Override
		public Identifier getId() {
			return ID;
		}

		@Override
		public Conditions conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
			ObjectBuilderTestConstants.LOGGER.info("Loading custom criterion in advancement!");
			return super.conditionsFromJson(jsonObject, advancementEntityPredicateDeserializer);
		}
	}
}
