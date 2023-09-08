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

package net.fabricmc.fabric.api.object.builder.v1.trade;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.impl.object.builder.TradeOfferInternals;

/**
 * Utilities to help with registration of trade offers.
 */
public final class TradeOfferHelper {
	/**
	 * Registers trade offer factories for use by villagers. This registers the same trades regardless of
	 * whether the rebalanced trade experiment is enabled.
	 *
	 * @param profession the villager profession to assign the trades to
	 * @param level the profession level the villager must be to offer the trades
	 * @param factories a consumer to provide the factories
	 * @deprecated Use {@link #registerVillagerOffers(VillagerProfession, int, VillagerTradeRegistrationCallback)} instead.
	 */
	public static void registerVillagerOffers(VillagerProfession profession, int level, Consumer<List<TradeOffers.Factory>> factories) {
		TradeOfferInternals.registerVillagerOffers(profession, level, (trades, rebalanced) -> factories.accept(trades));
	}

	/**
	 * Registers trade offer factories for use by villagers. This allows mods to register different
	 * trades depending on whether the trades are for the rebalanced trade experiment.
	 *
	 * <p>Below is an example, of registering a trade offer factory to be added a blacksmith with a profession level of 3:
	 * <blockquote><pre>
	 * TradeOfferHelper.registerVillagerOffers(VillagerProfession.BLACKSMITH, 3, (factories, rebalanced) -> {
	 * 	factories.add(new CustomTradeFactory(...);
	 * });
	 * </pre></blockquote>
	 *
	 * @param profession the villager profession to assign the trades to
	 * @param level the profession level the villager must be to offer the trades
	 * @param factories a consumer to provide the factories
	 */
	public static void registerVillagerOffers(VillagerProfession profession, int level, VillagerTradeRegistrationCallback factories) {
		TradeOfferInternals.registerVillagerOffers(profession, level, factories);
	}

	/**
	 * Registers trade offer factories for use by wandering trades.
	 * If the rebalanced trade experiment is enabled, {@code level} is ignored,
	 * and a fixed number of randomly chosen trades registered by this method will always appear.
	 * This number is currently 25%; this is subject to change.
	 *
	 * @param level the level of the trades
	 * @param factory a consumer to provide the factories
	 * @deprecated Use {@link #registerWanderingTraderOffers(int, WanderingTraderTradeRegistrationCallback)} instead.
	 * Given the inherent design incompatibility that needs to be addressed by mod developers, this is deprecated for removal.
	 */
	@Deprecated(forRemoval = true)
	public static void registerWanderingTraderOffers(int level, Consumer<List<TradeOffers.Factory>> factory) {
		TradeOfferInternals.registerWanderingTraderOffers(level, factory);
	}

	/**
	 * Registers trade offer factories for use by wandering trades.
	 * If the rebalanced trade experiment is enabled, {@code level} is ignored.
	 * If the experiment is not enabled, the weight is ignored.
	 *
	 * @param level the level of the trades
	 * @param factory a consumer to provide the factories
	 */
	public static void registerWanderingTraderOffers(int level, WanderingTraderTradeRegistrationCallback factory) {
		TradeOfferInternals.registerWanderingTraderOffers(level, factory);
	}

	/**
	 * @deprecated This never did anything useful.
	 */
	@Deprecated(forRemoval = true)
	public static void refreshOffers() {
		TradeOfferInternals.printRefreshOffersWarning();
	}

	private TradeOfferHelper() {
	}

	@FunctionalInterface
	public interface VillagerTradeRegistrationCallback {
		/**
		 * Callback to register villager trades.
		 * @param trades the list to add trades to
		 * @param rebalanced whether the trades are for the rebalanced trade experiment
		 */
		void onRegister(List<TradeOffers.Factory> trades, boolean rebalanced);
	}

	@FunctionalInterface
	public interface WanderingTraderTradeRegistrationCallback {
		/**
		 * Callback to register weighted wandering trader trades.
		 *
		 * <p>A trade offer pool entry is an array of trades, and the number of rolls from the pool.
		 * If the number of rolls is equal to or above the size of the array, all trades are included.
		 * @param trades the list to add trade offer pool entries to
		 * @param rebalanced whether the trades are for the rebalanced trade experiment
		 */
		void onRegister(List<Pair<TradeOffers.Factory[], Integer>> trades, boolean rebalanced);
	}
}
