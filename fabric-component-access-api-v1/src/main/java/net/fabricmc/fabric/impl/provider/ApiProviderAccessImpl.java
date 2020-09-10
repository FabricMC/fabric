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

package net.fabricmc.fabric.impl.provider;

import java.util.function.Function;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.ApiProviderAccess;
import net.fabricmc.fabric.api.provider.v1.BlockEntityProviderFunction;
import net.fabricmc.fabric.api.provider.v1.BlockProviderFunction;

public final class ApiProviderAccessImpl<P extends ApiProvider<P, A>, A> implements ApiProviderAccess<P, A> {
	private final A absentApi;
	private final P absentProvider;
	private final Class<A> apiType;

	private final Reference2ReferenceOpenHashMap<Block, BlockProviderFunction<P, A>> blockMappings = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);
	private final Reference2ReferenceOpenHashMap<BlockEntityType<?>, BlockEntityProviderFunction<P, A>> blockEntityMappings = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);
	private final Reference2ReferenceOpenHashMap<EntityType<?>, Function<Entity, P>> entityMappings = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);
	private final Reference2ReferenceOpenHashMap<Item, Function<ItemStack, P>> itemMappings = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	ApiProviderAccessImpl(Class<A> apiType, P absentProvider) {
		absentApi = absentProvider.getApi();
		this.absentProvider = absentProvider;
		this.apiType = apiType;
		blockEntityMappings.defaultReturnValue(be -> getProviderFromBlock(be.getWorld(), be.getPos(), be.getCachedState()));
		blockMappings.defaultReturnValue((w, p, s) -> absentProvider);
		entityMappings.defaultReturnValue(e -> absentProvider);
	}

	@Override
	public A absentApi() {
		return absentApi;
	}

	@Override
	public P absentProvider() {
		return absentProvider;
	}

	@Override
	public Class<A> apiType() {
		return apiType;
	}

	@Override
	public A castToApi(Object obj) {
		return apiType.cast(obj);
	}

	@Override
	public P getProviderFromBlock(World world, BlockPos pos, BlockState blockState) {
		return blockMappings.get(blockState.getBlock()).getProvider(world, pos, blockState);
	}

	@Override
	public P getProviderFromBlockEntity(BlockEntity blockEntity) {
		return blockEntityMappings.get(blockEntity.getType()).getProvider(blockEntity);
	}

	@Override
	public P getProviderFromEntity(Entity entity) {
		return entityMappings.get(entity.getType()).apply(entity);
	}

	@Override
	public P getProviderFromStack(ItemStack stack) {
		return itemMappings.get(stack.getItem()).apply(stack);
	}

	@Override
	public void registerBlockProvider(BlockProviderFunction<P, A> mapping, Block... blocks) {
		for (final Block b : blocks) {
			if (blockMappings.putIfAbsent(b, mapping) != null) {
				LOGGER.warn("[Fabric] Encountered duplicate Block API Provider registration for block " + Registry.BLOCK.getId(b));
			}
		}
	}

	@Override
	public void registerBlockEntityProvider(BlockEntityProviderFunction<P, A> mapping, BlockEntityType<?>... blockEntityTypes) {
		for (final BlockEntityType<?> bet : blockEntityTypes) {
			if (blockEntityMappings.putIfAbsent(bet, mapping) != null) {
				LOGGER.warn("[Fabric] Encountered duplicate BlockEntity API Provider registration for block entity type " + Registry.BLOCK_ENTITY_TYPE.getId(bet));
			}
		}
	}

	@Override
	public void registerEntityProvider(Function<Entity, P> mapping, EntityType<?> entityType) {
		if (entityMappings.putIfAbsent(entityType, mapping) != null) {
			LOGGER.warn("[Fabric] Encountered duplicate Block API Provider registration for entity " + Registry.ENTITY_TYPE.getId(entityType));
		}
	}

	@Override
	public void registerItemProvider(Function<ItemStack, P> mapping, Item... items) {
		for (final Item item : items) {
			if (itemMappings.putIfAbsent(item, mapping) != null) {
				LOGGER.warn("[Fabric] Encountered duplicate Item API Provider registration for item " + Registry.ITEM.getId(item));
			}
		}
	}

	private static final Logger LOGGER = LogManager.getLogger();
}
