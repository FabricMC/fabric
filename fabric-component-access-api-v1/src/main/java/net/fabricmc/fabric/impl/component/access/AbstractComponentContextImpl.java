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

import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.component.access.v1.ComponentAccess;
import net.fabricmc.fabric.api.component.access.v1.ComponentContext;
import net.fabricmc.fabric.api.component.access.v1.ComponentType;

@SuppressWarnings("rawtypes")
abstract class AbstractComponentContextImpl implements ComponentContext, ComponentAccess {
	protected ComponentTypeImpl componentType;
	protected Function<ComponentContext, ?> mapping;
	protected World world;
	protected Identifier id;
	protected Direction side;

	@Override
	public final Object get(Direction side, Identifier id) {
		this.side = side;
		this.id = id;
		return ObjectUtils.defaultIfNull(mapping.apply(this), componentType.absent());
	}

	@Override
	public final ComponentType componentType() {
		return componentType;
	}

	@Override
	public final Identifier id() {
		return id;
	}

	@Override
	public final Direction side() {
		return side;
	}

	protected abstract World getWorldLazily();

	@Override
	public final World world() {
		World result = world;

		if (result == null) {
			result = getWorldLazily();
			world = result;
		}

		return result;
	}
}
