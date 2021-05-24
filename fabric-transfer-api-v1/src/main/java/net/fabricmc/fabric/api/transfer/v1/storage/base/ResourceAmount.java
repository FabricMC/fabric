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

package net.fabricmc.fabric.api.transfer.v1.storage.base;

import java.util.Objects;

/**
 * An immutable object storing both a resource and an amount, provided for convenience.
 * @param <T> The type of the stored resource.
 */
public class ResourceAmount<T> {
	public final T resource;
	public final long amount;

	public ResourceAmount(T resource, long amount) {
		this.resource = resource;
		this.amount = amount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResourceAmount<?> that = (ResourceAmount<?>) o;
		return amount == that.amount && Objects.equals(resource, that.resource);
	}

	@Override
	public int hashCode() {
		return Objects.hash(resource, amount);
	}
}
