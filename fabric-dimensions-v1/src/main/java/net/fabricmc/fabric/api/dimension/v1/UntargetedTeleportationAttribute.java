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

package net.fabricmc.fabric.api.dimension.v1;

import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;

public final class UntargetedTeleportationAttribute<T> {
	private final String id;

	private final Class<T> valueClass;

	private UntargetedTeleportationAttribute(String id, Class<T> valueClass) {
		Preconditions.checkNotNull(id, "id must not be null");
		Preconditions.checkNotNull(valueClass, "valueClass must not be null");
		Preconditions.checkArgument(id.toLowerCase(Locale.ROOT).equals(id), "id must be lowercase");
		this.id = id;
		this.valueClass = valueClass;
	}

	/**
	 * The id given to this attribute. Always lowercase.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the class of values associated with this attribute.
	 */
	public Class<T> getValueClass() {
		return valueClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UntargetedTeleportationAttribute<?> that = (UntargetedTeleportationAttribute<?>) o;
		return id.equals(that.id)
				&& valueClass.equals(that.valueClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, valueClass);
	}
}
