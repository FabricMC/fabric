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

package net.fabricmc.fabric.impl.object.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

public final class TradeOfferInternals {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-object-builder-api-v1");

	private TradeOfferInternals() {
	}

	/**
	 * Make the rebalanced profession map modifiable, then copy all vanilla
	 * professions' trades to prevent modifications from propagating to the rebalanced one.
	 */
	private static void initVillagerTrades() {
		if (!(TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE instanceof HashMap)) {
			Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> map = new HashMap<>(TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE);

			for (Map.Entry<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> trade : TradeOffers.PROFESSION_TO_LEVELED_TRADE.entrySet()) {
				if (!map.containsKey(trade.getKey())) map.put(trade.getKey(), trade.getValue());
			}

			TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE = map;
		}
	}

	// synchronized guards against concurrent modifications - Vanilla does not mutate the underlying arrays (as of 1.16),
	// so reads will be fine without locking.
	public static synchronized void registerVillagerOffers(VillagerProfession profession, int level, TradeOfferHelper.VillagerOffersAdder factory) {
		Objects.requireNonNull(profession, "VillagerProfession may not be null.");
		initVillagerTrades();
		registerOffers(TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>()), level, trades -> factory.onRegister(trades, false));
		registerOffers(TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>()), level, trades -> factory.onRegister(trades, true));
	}

	public static synchronized void registerWanderingTraderOffers(int level, Consumer<List<TradeOffers.Factory>> factory) {
		registerOffers(TradeOffers.WANDERING_TRADER_TRADES, level, factory);
	}

	// Shared code to register offers for both villagers and wandering traders.
	private static void registerOffers(Int2ObjectMap<TradeOffers.Factory[]> leveledTradeMap, int level, Consumer<List<TradeOffers.Factory>> factory) {
		final List<TradeOffers.Factory> list = new ArrayList<>();
		factory.accept(list);

		final TradeOffers.Factory[] originalEntries = leveledTradeMap.computeIfAbsent(level, key -> new TradeOffers.Factory[0]);
		final TradeOffers.Factory[] addedEntries = list.toArray(new TradeOffers.Factory[0]);

		final TradeOffers.Factory[] allEntries = ArrayUtils.addAll(originalEntries, addedEntries);
		leveledTradeMap.put(level, allEntries);
	}

	public static void printRefreshOffersWarning() {
		Throwable loggingThrowable = new Throwable();
		LOGGER.warn("TradeOfferHelper#refreshOffers does not do anything, yet it was called! Stack trace:", loggingThrowable);
	}

	public static class WanderingTraderOffersBuilderImpl implements TradeOfferHelper.WanderingTraderOffersBuilder {
		/**
		 * Make the trade list modifiable.
		 */
		static void initWanderingTraderTrades() {
			if (!(TradeOffers.REBALANCED_WANDERING_TRADER_TRADES instanceof ArrayList)) {
				TradeOffers.REBALANCED_WANDERING_TRADER_TRADES = new ArrayList<>(TradeOffers.REBALANCED_WANDERING_TRADER_TRADES);
			}
		}

		@Override
		public TradeOfferHelper.WanderingTraderOffersBuilder pool(int count, TradeOffers.Factory... factories) {
			if (factories.length == 0) throw new IllegalArgumentException("cannot add empty pool");
			if (count <= 0) throw new IllegalArgumentException("count must be positive");

			Pair<TradeOffers.Factory[], Integer> pool = Pair.of(factories, count);
			initWanderingTraderTrades();
			TradeOffers.REBALANCED_WANDERING_TRADER_TRADES.add(pool);
			return this;
		}

		@Override
		public TradeOfferHelper.WanderingTraderOffersBuilder addOffersToPool(int poolIndex, TradeOffers.Factory... factories) {
			Objects.checkIndex(poolIndex, TradeOffers.REBALANCED_WANDERING_TRADER_TRADES.size());
			initWanderingTraderTrades();
			Pair<TradeOffers.Factory[], Integer> pool = TradeOffers.REBALANCED_WANDERING_TRADER_TRADES.get(poolIndex);
			TradeOffers.Factory[] modified = ArrayUtils.addAll(pool.getLeft(), factories);
			TradeOffers.REBALANCED_WANDERING_TRADER_TRADES.set(poolIndex, Pair.of(modified, pool.getRight()));
			return this;
		}
	}
}
