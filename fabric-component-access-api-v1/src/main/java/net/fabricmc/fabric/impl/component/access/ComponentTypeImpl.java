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

import java.util.IdentityHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.component.access.v1.BlockComponentContext;
import net.fabricmc.fabric.api.component.access.v1.ComponentAccess;
import net.fabricmc.fabric.api.component.access.v1.ComponentType;
import net.fabricmc.fabric.api.component.access.v1.EntityComponentContext;
import net.fabricmc.fabric.api.component.access.v1.ItemComponentContext;

public final class ComponentTypeImpl<T> implements ComponentType<T> {
	private final T absent;
	private final Function<BlockComponentContext, ?> defaultBlockMapping;
	private final Function<ItemComponentContext, ?> defaultItemMapping;
	private final Function<EntityComponentContext, ?> defaultEntityMapping;

	private final IdentityHashMap<Block, Function<BlockComponentContext, ?>> blockMappings = new IdentityHashMap<>();
	private final IdentityHashMap<Item, Function<ItemComponentContext, ?>> itemMappings = new IdentityHashMap<>();
	private final IdentityHashMap<EntityType<?>, Function<EntityComponentContext, ?>> entityMappings = new IdentityHashMap<>();
	private final IdentityHashMap<Item, ObjectArrayList<BiPredicate<ItemComponentContext, T>>> itemActions = new IdentityHashMap<>();

	private ObjectArrayList<Pair<Function<EntityComponentContext, T>, Predicate<EntityType<?>>>> deferedEntityMappings;

	private final AbsentComponentAccess<T> absentComponentAccess;
	private final Class<T> type;

	ComponentTypeImpl(Class<T> type, T absent) {
		this.absent = absent;
		this.type = type;
		defaultBlockMapping = b -> absent;
		defaultItemMapping = i -> absent;
		defaultEntityMapping = e -> absent;
		absentComponentAccess = new AbsentComponentAccess<>(this);
	}

	@Override
	public T absent() {
		return absent;
	}

	@Override
	public T cast(Object obj) {
		return type.cast(obj);
	}

	Function<BlockComponentContext, ?> getMapping(Block block) {
		return blockMappings.getOrDefault(block, defaultBlockMapping);
	}

	@Override
	public void registerBlockProvider(Function<BlockComponentContext, T> mapping, Block... blocks) {
		for (final Block b : blocks) {
			blockMappings.put(b, mapping);
		}
	}

	Function<EntityComponentContext, ?> getMapping(Entity entity) {
		return entityMappings.getOrDefault(entity.getType(), defaultEntityMapping);
	}

	@Override
	public void registerEntityProvider(Function<EntityComponentContext, T> mapping, EntityType<?>... entities) {
		for (final EntityType<?> e : entities) {
			entityMappings.put(e, mapping);
		}
	}

	Function<ItemComponentContext, ?> getMapping(Item item) {
		return itemMappings.getOrDefault(item, defaultItemMapping);
	}

	@Override
	public void registerItemProvider(Function<ItemComponentContext, T> mapping, Item... items) {
		for (final Item i : items) {
			itemMappings.put(i, mapping);
		}
	}

	@Override
	public void registerItemAction(BiPredicate<ItemComponentContext, T> action, Item... items) {
		for (final Item i : items) {
			ObjectArrayList<BiPredicate<ItemComponentContext, T>> list = itemActions.get(i);

			if (list == null) {
				list = new ObjectArrayList<>(4);
				itemActions.put(i, list);
			}

			list.add(action);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentAccess<T> getAccess(World world, BlockPos pos) {
		return BlockComponentContextImpl.get(this, world, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentAccess<T> getAccess(World world, BlockPos pos, BlockState blockState) {
		return BlockComponentContextImpl.get(this, world, pos, blockState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentAccess<T> getAccess(BlockEntity blockEntity) {
		return BlockComponentContextImpl.get(this, blockEntity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Entity> ComponentAccess<T> getAccess(E entity) {
		applyDeferredEntityRegistrations();
		return EntityComponentContextImpl.get(this, entity);
	}

	@Override
	public ComponentAccess<T> getAbsentAccess() {
		return absentComponentAccess;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentAccess<T> getAccessForHeldItem(Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, ServerPlayerEntity player) {
		return ItemComponentContextImpl.get(this, stackGetter, stackSetter, player);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentAccess<T> getAccess(Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, World world) {
		return ItemComponentContextImpl.get(this, stackGetter, stackSetter, world);
	}

	@Override
	public boolean applyActions(T target, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, ServerPlayerEntity player) {
		final ObjectArrayList<BiPredicate<ItemComponentContext, T>> actions = itemActions.get(stackGetter.get().getItem());

		if (actions != null && !actions.isEmpty()) {
			return applyActions(target, actions, ItemComponentContextImpl.get(this, stackGetter, stackSetter, player));
		}

		return false;
	}

	@Override
	public boolean applyActions(T target, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter, World world) {
		final ObjectArrayList<BiPredicate<ItemComponentContext, T>> actions = itemActions.get(stackGetter.get().getItem());

		if (actions != null && !actions.isEmpty()) {
			return applyActions(target, actions, ItemComponentContextImpl.get(this, stackGetter, stackSetter, world));
		}

		return false;
	}

	private boolean applyActions(T target, ObjectArrayList<BiPredicate<ItemComponentContext, T>> actions, ItemComponentContext ctx) {
		for (final BiPredicate<ItemComponentContext, T> action : actions) {
			if (action.test(ctx, target)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void registerEntityProvider(Function<EntityComponentContext, T> mapping, Predicate<EntityType<?>> predicate) {
		if (deferedEntityMappings == null) {
			deferedEntityMappings = new ObjectArrayList<>();
		}

		deferedEntityMappings.add(Pair.of(mapping, predicate));
	}

	private void applyDeferredEntityRegistrations() {
		if (deferedEntityMappings != null) {
			for (final Pair<Function<EntityComponentContext, T>, Predicate<EntityType<?>>> pair : deferedEntityMappings) {
				Registry.ENTITY_TYPE.forEach(e -> {
					if (pair.getSecond().test(e)) {
						registerEntityProvider(pair.getFirst(), e);
					}
				});
			}

			deferedEntityMappings = null;
		}
	}
}
