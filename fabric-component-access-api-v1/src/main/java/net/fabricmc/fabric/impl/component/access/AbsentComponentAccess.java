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

package net.fabricmc.fabric.impl.component.access;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.component.access.v1.ComponentAccess;
import net.fabricmc.fabric.api.component.access.v1.ComponentType;

public final class AbsentComponentAccess<T> implements ComponentAccess<T> {
	protected final ComponentType<T> componentType;

	AbsentComponentAccess(ComponentType<T> componentType) {
		this.componentType = componentType;
	}

	@Override
	public T get(Direction side, Identifier id) {
		return componentType.absent();
	}

	@Override
	public ComponentType<T> componentType() {
		return componentType;
	}
}
