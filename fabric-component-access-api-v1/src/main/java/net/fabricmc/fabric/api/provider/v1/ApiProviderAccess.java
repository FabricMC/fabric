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

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Describes and provides access to component instances that may be retrieved
 * for blocks, items or entities.
 *
 * <p>This interface should never be implemented by mod authors. Create new instances
 * using {@link ApiProviderAccessRegistry#createAccess(net.minecraft.util.Identifier, Class, ApiProvider)}.
 *
 * @param <P> Identifies the API provider type
 * @param <A> Identifies the API type
 */
public interface ApiProviderAccess<P extends ApiProvider<P, A>, A> {
	/**
	 * Component value that will be returned when  an API is not present or not available.
	 *
	 * @return value to be returned when an API is not present or not available.
	 */
	A absentApi();

	/**
	 * An automatically constructed, immutable and non-allocating {@code ComponentAccess} instance
	 * that will always return the {@link #absentApi()} value.  Useful as default return value for access requests.
	 *
	 * @return an immutable, non-allocating {@code ComponentAccess} instance that will always return {@link #absentApi()}
	 */
	P absentProvider();

	/**
	 * The class associated with this component type. Exposed to support introspection.
	 *
	 * @return the class associated with this component type
	 */
	Class<A> apiType();

	/**
	 * Casts the input parameter to the component class associated with this component type.
	 *
	 * @param obj the object to be cast
	 * @return the input object cast to the component type
	 *
	 * @throws ClassCastException if the input object cannot be cast to the component class
	 */
	A castToApi(Object obj);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * <p>Note that {@link #getProviderFromBlock(World, BlockPos, BlockState)} may be more performant
	 * if 1) you know this component type requires block state and 2) the block state
	 * and the given position is already on the call stack.
	 *
	 * @param world the server world where the component may be located
	 * @param pos the block position where the component may be located
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location
	 */
	default P getProviderFromBlock(World world, BlockPos pos) {
		return getProviderFromBlock(world, pos, world.getBlockState(pos));
	}

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * @param world the server world where the component may be located
	 * @param pos the block position where the component may be located
	 * @param blockState the current block state at the given position within the world
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location
	 */
	P getProviderFromBlock(World world, BlockPos pos, BlockState blockState);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location.
	 *
	 * <p>If the API consumer somehow knows the block entity implements the
	 * provider interface directly, casting the BE instance will always be
	 * faster. This is useful when that is unknown to the consumer, or when
	 * the BlockEntity exposes the target API as a member.
	 *
	 * @param blockEntity the block entity where the component may be located
	 * @return a {@code ComponentAccess} to access components of this type
	 */
	P getProviderFromBlockEntity(BlockEntity blockEntity);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present in the given entity.
	 *
	 * <p>If the API consumer somehow knows the entity implements the
	 * provider interface directly, casting the entity instance will always be
	 * faster. This is useful when that is unknown to the consumer, or when
	 * the Entity exposes the target API as a member.
	 *
	 * @param entity entity to provide component access if available
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present in the given entity
	 */
	P getProviderFromEntity(Entity entity);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present in the held in the main hand of the given player.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * @param player the player that may be holding an item containing a component of this type
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present in the held item
	 */
	default P getProviderFromHeldItem(ServerPlayerEntity player) {
		return getProviderFromStack(player.getMainHandStack());
	}

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present in an item stack not held by a player.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * @param stackGetter function that will be be used to retrieve item stack for component state
	 * @param stackSetter function that will be be used to persist item stack for component state
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present in the item stack
	 */
	P getProviderFromStack(ItemStack stack);


	/**
	 * Causes the given blocks to provide component instances of this type
	 * by application of the given mapping function. Use this version for blocks that
	 * may provide a component without the presence of a {@code BlockEntity}.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping function that derives a component instance from an access context
	 * @param blocks one or more blocks for which the function will apply
	 */
	void registerBlockProvider(BlockProviderFunction<P, A> mapping, Block... blocks);

	/**
	 * Causes the given blocks to provide component instances of this type
	 * via block entities associated with the given blocks.
	 *
	 * <p>Use this version for blocks where the {@code BlockEntity} houses or
	 * directly implements the component instance.
	 *
	 * @param blocks one or more blocks that will provide components in this way
	 */
	void registerBlockEntityProvider(BlockEntityProviderFunction<P, A> mapping, BlockEntityType<?>... blockEntityTypes);

	/**
	 * Causes the given entity types to provide component instances of this type
	 * by application of the given mapping function.
	 *
	 * <p>This will override any previous mapping of the same component type and only one
	 * result per entity is possible.  For the reason, mod authors are advised to create
	 * distinct component types for their use cases as needed to prevent conflicts.
	 *
	 * @param mapping mapping function that derives a component instance from an access context
	 * @param entities one or more entities for which the function will apply
	 */
	void registerEntityProvider(Function<Entity, P> mapping, EntityType<?> entityType);

	/**
	 * Causes the given items to provide component instances of this type
	 * by application of the given mapping function to item stacks having the given items.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping mapping function that derives a component instance from an access context
	 * @param items one or more items for which the function will apply
	 */
	void registerItemProvider(Function<ItemStack, P> mapping, Item... items);
}
