/*
 * Copyright (c) 2016, 2017, 2018, 2019, 2020 FabricMC
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

package net.fabricmc.fabric.api.util;

import java.util.Objects;

import net.minecraft.util.Identifier;

public final class NbtIdentifier extends Identifier {
	public final int type;

	public NbtIdentifier(String[] id, int type) {
		super(id);
		if (type < 0 || type > NbtType.LONG_ARRAY) throw new IllegalArgumentException("Invalid NBT type: " + type);
		this.type = type;
	}

	public NbtIdentifier(String id, int type) {
		super(id);
		if (type < 0 || type > NbtType.LONG_ARRAY) throw new IllegalArgumentException("Invalid NBT type: " + type);
		this.type = type;
	}

	public NbtIdentifier(String namespace, String path, int type) {
		super(namespace, path);
		if (type < 0 || type > NbtType.LONG_ARRAY) throw new IllegalArgumentException("Invalid NBT type: " + type);
		this.type = type;
	}

	@Override
	public int hashCode() {
		int result = this.type;
		result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
		result = 31 * result + (this.path != null ? this.path.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NbtIdentifier)) return false;
		NbtIdentifier that = (NbtIdentifier) o;
		return this.type == that.type && Objects.equals(this.namespace, that.namespace) && Objects.equals(this.path, that.path);
	}

	@Override
	public String toString() {
		return this.namespace + ":" + this.path + " of " + this.type;
	}
}
