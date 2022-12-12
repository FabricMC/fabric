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

package net.fabricmc.fabric.api.datagen.v1.builder;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

import net.minecraft.util.Identifier;

/**
 * Instantiate this class in order to provide an optional set of <code>overrides</code> for a given item model JSON.
 */
public class OverrideBuilder {
	private final Identifier model;
	private final Object2FloatMap<Identifier> predicates = new Object2FloatLinkedOpenHashMap<>();

	/**
	 * Create a new override builder with a given model ID to switch to for this item.
	 *
	 * @param overrideModel The ID of the model to be overridden by.
	 */
	public OverrideBuilder(Identifier overrideModel) {
		this.model = overrideModel;
	}

	/**
	 * Adds a new predicate set to dictate when to switch to the provided item model.
	 *
	 * @param key The ID of an item property to check for.
	 * @param value The value of the property for which the override should be carried out.
	 * @return The current newly-modified {@link OverrideBuilder} instance.
	 */
	public OverrideBuilder predicate(Identifier key, float value) {
		this.predicates.putIfAbsent(key, value);
		return this;
	}

	public JsonObject build() {
		JsonObject override = new JsonObject();

		JsonObject predicates = new JsonObject();
		this.predicates.forEach((key, val) -> predicates.addProperty(key.toString(), val));
		override.add("predicate", predicates);

		override.addProperty("model", this.model.toString());
		return override;
	}
}
