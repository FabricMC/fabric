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

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.component.access.v1.ItemComponentContext;

@SuppressWarnings("rawtypes")
public final class ItemComponentContextImpl extends AbstractComponentContextImpl implements ItemComponentContext {
	private Supplier<ItemStack> stackGetter;
	private Consumer<ItemStack> stackSetter;
	private ServerPlayerEntity player;

	@Override
	public Supplier<ItemStack> stackGetter() {
		return stackGetter;
	}

	@Override
	public Consumer<ItemStack> stackSetter() {
		return stackSetter;
	}

	@Override
	public ServerPlayerEntity player() {
		return player;
	}

	@Override
	protected World getWorldLazily() {
		return player == null ? null : player.world;
	}

	@SuppressWarnings("unchecked")
	private ItemComponentContextImpl prepare(ComponentTypeImpl componentType, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, World world) {
		this.componentType = componentType;
		this.stackSetter = stackSetter;
		this.stackGetter = stackGetter;
		this.world = world;
		player = null;
		mapping = componentType.getMapping(stackGetter.get().getItem());
		return this;
	}

	@SuppressWarnings("unchecked")
	private ItemComponentContextImpl prepare(ComponentTypeImpl componentType, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, ServerPlayerEntity player) {
		this.componentType = componentType;
		this.stackSetter = stackSetter;
		this.stackGetter = stackGetter;
		this.player = player;
		world = player.world;
		mapping = componentType.getMapping(stackGetter.get().getItem());
		return this;
	}

	private static final ThreadLocal<ItemComponentContextImpl> POOL = ThreadLocal.withInitial(ItemComponentContextImpl::new);

	static ItemComponentContextImpl get(ComponentTypeImpl componentType, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, World world) {
		return POOL.get().prepare(componentType, stackGetter, stackSetter, world);
	}

	static ItemComponentContextImpl get(ComponentTypeImpl componentType, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, ServerPlayerEntity player) {
		return POOL.get().prepare(componentType, stackGetter, stackSetter, player);
	}
}
