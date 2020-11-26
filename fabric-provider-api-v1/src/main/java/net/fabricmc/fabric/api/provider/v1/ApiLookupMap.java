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

package net.fabricmc.fabric.api.provider.v1;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.provider.ApiLookupMapImpl;

/**
 * The building block for creating your own Provider.
 * You should store an instance of this interface in a static variable.
 */
public interface ApiLookupMap<L> extends Iterable<L> {
	static <L> ApiLookupMap<L> create(Supplier<L> lookupFactory) {
		Objects.requireNonNull(lookupFactory, "Lookup factory cannot be null");

		return new ApiLookupMapImpl<>(lookupFactory);
	}

	L getLookup(Identifier lookupId, Class<?> apiClass, Class<?> contextClass);
}
