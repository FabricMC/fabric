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

package net.fabricmc.fabric.impl.conditionalresource;

import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.conditionalresource.v1.ResourceCondition;

@ApiStatus.Internal
public final class ResourceConditionsImpl {
	private ResourceConditionsImpl() {
	}

	private static final BiMap<Identifier, ResourceCondition> CONDITIONS = HashBiMap.create();

	public static <T extends ResourceCondition> void register(Identifier id, T condition) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(condition);

		CONDITIONS.put(id, condition);
	}

	@Nullable
	public static <T extends ResourceCondition> T get(Identifier id) {
		Objects.requireNonNull(id);

		return (T) CONDITIONS.get(id);
	}

	@Nullable
	public static <T extends ResourceCondition> Identifier getId(T condition) {
		Objects.requireNonNull(condition);

		return CONDITIONS.inverse().get(condition);
	}

	public static boolean evaluate(Identifier resourceId, JsonObject object) {
		Identifier type = new Identifier(JsonHelper.getString(object, "type"));
		ResourceCondition condition = get(type);
		if (condition == null) throw new NullPointerException("Condition '" + type + "' does not exist!");

		return condition.process(resourceId, object.has("condition") ? object.get("condition") : null);
	}
}
