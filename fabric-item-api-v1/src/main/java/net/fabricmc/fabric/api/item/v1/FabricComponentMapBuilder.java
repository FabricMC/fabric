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

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.minecraft.component.ComponentType;

public interface FabricComponentMapBuilder {
	@Contract("_, _ -> new")
	default <T> T getOrCreate(ComponentType<T> type, Supplier<T> defaultCreator) {
		throw new AssertionError("Implemented in Mixin");
	}

	@Contract("_, _ -> new")
	default <T> T getOrDefault(ComponentType<T> type, @Nullable T defaultValue) {
		return getOrCreate(type, () -> defaultValue);
	}

	@Contract("_ -> new")
	default <T> List<T> getOrEmpty(ComponentType<List<T>> type) {
		throw new AssertionError("Implemented in Mixin");
	}
}
