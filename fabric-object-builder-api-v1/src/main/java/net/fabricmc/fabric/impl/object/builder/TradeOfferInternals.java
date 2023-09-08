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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

public final class TradeOfferInternals {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-object-builder-api-v1");

	private TradeOfferInternals() {
	}

	// synchronized guards against concurrent modifications - Vanilla does not mutate the underlying arrays (as of 1.16),
	// so reads will be fine without locking.
	public static synchronized void registerVillagerOffers(VillagerProfession profession, int level, TradeOfferHelper.VillagerTradeRegistrationCallback factory) {
		Objects.requireNonNull(profession, "VillagerProfession may not be null.");

		// Make the map modifiable
		if (!(TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE instanceof HashMap)) {
			TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE = new HashMap<>(TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE);
		}

		registerOffers(TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> {
			// Absence of the trade entry in rebalanced map means "check normal trade map".
			// If we just add an empty map, vanilla trades would not be available if rebalanced trade is used.
			// Copy the vanilla map here instead. Successive calls modify the copy only.
			final Int2ObjectMap<TradeOffers.Factory[]> vanillaMap = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);

			if (vanillaMap != null) {
				return new Int2ObjectOpenHashMap<>(vanillaMap);
			}

			// Custom profession; vanilla trades unavailable, so just make a new map.
			return new Int2ObjectOpenHashMap<>();
		}), level, factory::onRegister, true); // casting functional interface
		// This must be done AFTER the rebalanced trade map is changed, to avoid double registration
		// for the first call (by copying the already-registered map).
		registerOffers(TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>()), level, factory::onRegister, false);
	}

	public static synchronized void registerWanderingTraderOffers(int level, Consumer<List<TradeOffers.Factory>> factory) {
		registerOffers(TradeOffers.WANDERING_TRADER_TRADES, level, (trades, rebalanced) -> factory.accept(trades), false); // rebalanced arg unused

		// Rebalanced wandering trader offers are not leveled.
		registerRebalancedWanderingTraderOffers(poolList -> {
			final List<TradeOffers.Factory> list = new ArrayList<>();
			factory.accept(list);
			// The likely intent of the mod is to offer some of the entries, but not all.
			// This was previously done by entirely random offer; now that fixed-count pool is
			// used, if we add elements of the list one by one, they would all show up.
			// Offer 25% (arbitrary number chosen by apple502j) of the registered trades at a time.
			// Wandering traders are not Amazon.
			poolList.add(Pair.of(list.toArray(TradeOffers.Factory[]::new), MathHelper.ceil(list.size() / 4.0)));
		});
	}

	public static synchronized void registerWanderingTraderOffers(int level, TradeOfferHelper.WanderingTraderTradeRegistrationCallback callback) {
		registerOffers(TradeOffers.WANDERING_TRADER_TRADES, level, (list, rebalanced) -> {
			List<Pair<TradeOffers.Factory[], Integer>> trades = new ArrayList<>();
			callback.onRegister(trades, false);
			trades.forEach(trade -> list.addAll(Arrays.asList(trade.getLeft())));
		}, false); // rebalanced arg unused

		// Rebalanced wandering trader offers are not leveled.
		registerRebalancedWanderingTraderOffers(poolList -> callback.onRegister(poolList, true));
	}

	// Shared code to register offers for both villagers and non-rebalanced wandering traders.
	private static void registerOffers(Int2ObjectMap<TradeOffers.Factory[]> leveledTradeMap, int level, BiConsumer<List<TradeOffers.Factory>, Boolean> factory, boolean rebalanced) {
		final List<TradeOffers.Factory> list = new ArrayList<>();
		factory.accept(list, rebalanced);

		final TradeOffers.Factory[] originalEntries = leveledTradeMap.computeIfAbsent(level, key -> new TradeOffers.Factory[0]);
		final TradeOffers.Factory[] addedEntries = list.toArray(new TradeOffers.Factory[0]);

		final TradeOffers.Factory[] allEntries = ArrayUtils.addAll(originalEntries, addedEntries);
		leveledTradeMap.put(level, allEntries);
	}

	private static void registerRebalancedWanderingTraderOffers(Consumer<List<Pair<TradeOffers.Factory[], Integer>>> factory) {
		// Make the list modifiable
		if (!(TradeOffers.REBALANCED_WANDERING_TRADER_TRADES instanceof ArrayList)) {
			TradeOffers.REBALANCED_WANDERING_TRADER_TRADES = new ArrayList<>(TradeOffers.REBALANCED_WANDERING_TRADER_TRADES);
		}

		factory.accept(TradeOffers.REBALANCED_WANDERING_TRADER_TRADES);
	}

	public static void printRefreshOffersWarning() {
		Throwable loggingThrowable = new Throwable();
		LOGGER.warn("TradeOfferHelper#refreshOffers does not do anything, yet it was called! Stack trace:", loggingThrowable);
	}
}
