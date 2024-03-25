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

package net.fabricmc.fabric.impl.lookup.block;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;

public final class BlockApiLookupImpl<A, C> implements BlockApiLookup<A, C> {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-lookup-api-v1/block");
	private static final ApiLookupMap<BlockApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(BlockApiLookupImpl::new);

	@SuppressWarnings("unchecked")
	public static <A, C> BlockApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
		return (BlockApiLookup<A, C>) LOOKUPS.getLookup(lookupId, apiClass, contextClass);
	}
	@ApiStatus.Internal
	public static <A,C>  Event<BlockApiProvider<A,C>> newEvent() {
		return EventFactory.createArrayBacked(BlockApiProvider.class, providers -> (world, pos, state, blockEntity, context) -> {
			for (BlockApiProvider<A, C> provider : providers) {
				A api = provider.find(world, pos, state, blockEntity, context);
				if (api!=null)return api;
			}
			return null;
		});
	}
	private final Identifier identifier;
	private final Class<A> apiClass;
	private final Class<C> contextClass;
	/**
	 * @deprecated see {@link #blockSpecific}
	 */
	@Deprecated(forRemoval = true)
	private final ApiProviderMap<Block, BlockApiProvider<A, C>> providerMap = ApiProviderMap.create();
	private final Event<BlockApiProvider<A,C>> preliminary = newEvent();
	private final ApiProviderMap<Block,Event<BlockApiProvider<A,C>>> blockSpecific = ApiProviderMap.create();
	/**
	 * It can't reflect phase order.
	 */
	@ApiStatus.Experimental
	private final Multimap<Block,BlockApiProvider<A, C>> blockSpecificProviders = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().arrayListValues().build());
	private final Event<BlockApiProvider<A,C>> fallback = newEvent();
	/**
	 * It can't reflect phase order.
	 */
	@ApiStatus.Experimental
	private final List<BlockApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

	@SuppressWarnings("unchecked")
	private BlockApiLookupImpl(Identifier identifier, Class<?> apiClass, Class<?> contextClass) {
		this.identifier = identifier;
		this.apiClass = (Class<A>) apiClass;
		this.contextClass = (Class<C>) contextClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerSelf(BlockEntityType<?>... blockEntityTypes) {
		for (BlockEntityType<?> blockEntityType : blockEntityTypes) {
			Block supportBlock = ((BlockEntityTypeAccessor) blockEntityType).getBlocks().iterator().next();
			Objects.requireNonNull(supportBlock, "Could not get a support block for block entity type.");
			BlockEntity blockEntity = blockEntityType.instantiate(BlockPos.ORIGIN, supportBlock.getDefaultState());
			Objects.requireNonNull(blockEntity, "Instantiated block entity may not be null.");

			if (!apiClass.isAssignableFrom(blockEntity.getClass())) {
				String errorMessage = String.format(
						"Failed to register self-implementing block entities. API class %s is not assignable from block entity class %s.",
						apiClass.getCanonicalName(),
						blockEntity.getClass().getCanonicalName()
				);
				throw new IllegalArgumentException(errorMessage);
			}
		}
		BlockApiLookup.registerForBlockEntities(this,(blockEntity, context) -> (A) blockEntity, blockEntityTypes );
	}

	@Override
	public void registerFallback(@NotNull BlockApiProvider<A, C> fallbackProvider) {
BlockApiLookup.super.registerFallback(fallbackProvider);
		fallbackProviders.add(fallbackProvider);
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	public Class<A> apiClass() {
		return apiClass;
	}

	@Override
	public Class<C> contextClass() {
		return contextClass;
	}

	@SuppressWarnings("removal")//though just override while no invoke, idea still warns
	@Override
	@Deprecated(forRemoval = true)

	public @Nullable BlockApiProvider<A, C> getProvider(Block block) {
		for (BlockApiProvider<A, C> provider : blockSpecificProviders.get(block)) {
			return provider;
		}
		return null;
	}

	public @UnmodifiableView List<BlockApiProvider<A, C>> getFallbackProviders() {
		return fallbackProviders;
	}

	@Override
	public Event<BlockApiProvider<A, C>> preliminary() {
		return preliminary;
	}

	@Override
	public @UnmodifiableView Map<Block, Event<BlockApiProvider<A, C>>> blockSpecific() {
		return blockSpecific.asMap();
	}
	@Override
	public @NotNull Event<BlockApiProvider<A, C>> getSpecificFor(Block block) {
		var event = blockSpecific.get(block);
		if (event==null){
			event=newEvent();
			blockSpecific.putIfAbsent(block,event);
		}
		return event;
	}
	@Override
	public Event<BlockApiProvider<A, C>> fallback() {
		return fallback;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <B extends BlockEntity> void registerForBlockEntity(@NotNull BlockEntityType<? extends B> blockEntityType, @NotNull BiFunction<? super B, ? super C,? extends @Nullable A> provider) {
		for (Block block : ((BlockEntityTypeAccessor) blockEntityType).getBlocks().toArray(new Block[0])) {
			getSpecificFor(block).register((world, pos, state, blockEntity, context) -> provider.apply((B)blockEntity,context));
		}
	}
}
