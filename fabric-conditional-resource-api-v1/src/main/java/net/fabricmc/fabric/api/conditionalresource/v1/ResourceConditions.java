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

package net.fabricmc.fabric.api.conditionalresource.v1;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.conditionalresource.ResourceConditionsImpl;

public final class ResourceConditions {
	private ResourceConditions() {
	}

	/**
	 * Registers a {@link ResourceCondition} with the specified {@code id}.
	 *
	 * @param id        the identifier of the condition
	 * @param condition the resource condition
	 * @param <T>       the {@link ResourceCondition} type
	 */
	public static <T extends ResourceCondition> void register(Identifier id, T condition) {
		ResourceConditionsImpl.register(id, condition);
	}

	/**
	 * Gets the {@link ResourceCondition} with the specified {@code id}.
	 *
	 * @param id  the identifier of the condition
	 * @param <T> the {@link ResourceCondition} type
	 * @return the resource condition
	 */
	@Nullable
	public static <T extends ResourceCondition> T get(Identifier id) {
		return ResourceConditionsImpl.get(id);
	}

	/**
	 * Gets the {@link Identifier} with the specified {@link ResourceCondition}.
	 *
	 * @param condition the resource condition
	 * @param <T>       the {@link ResourceCondition} type
	 * @return the identifier of the condition
	 */
	@Nullable
	public static <T extends ResourceCondition> Identifier getId(T condition) {
		return ResourceConditionsImpl.getId(condition);
	}

	/**
	 * Evaluates the resource condition.
	 *
	 * @param fabricMetaId the identifier of the fabric meta
	 * @param element      the content of the condition
	 * @return whether the condition is true
	 */
	public static boolean evaluate(Identifier fabricMetaId, JsonElement element) {
		if (!element.isJsonObject()) throw new IllegalArgumentException("Condition element is not an object!");
		return ResourceConditionsImpl.evaluate(fabricMetaId, element.getAsJsonObject());
	}
}
