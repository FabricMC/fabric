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

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.impl.object.builder.TradeOfferInternals;

/**
 * Utilities to help with registration of trade offers.
 */
public final class TradeOfferHelper {
	/**
	 * Registers trade offer factories for use by villagers.
	 *
	 * <p>Below is an example, of registering a trade off factory to be added a blacksmith with a profession level of 3:
	 * <blockquote><pre>
	 * TradeOfferHelper.registerVillagerOffers(VillagerProfession.BLACKSMITH, 3, factories -> {
	 * 	factories.add(new CustomTradeFactory(...);
	 * });
	 * </pre></blockquote>
	 *
	 * @param profession the villager profession to assign the trades to
	 * @param level the profession level the villager must be to offer the trades
	 * @param factories a consumer to provide the factories
	 */
	public static void registerVillagerOffers(VillagerProfession profession, int level, Consumer<List<TradeOffers.Factory>> factories) {
		TradeOfferInternals.registerVillagerOffers(profession, level, factories);
	}

	/**
	 * Registers trade offer factories for use by wandering trades.
	 *
	 * @param level the level the trades
	 * @param factory a consumer to provide the factories
	 */
	public static void registerWanderingTraderOffers(int level, Consumer<List<TradeOffers.Factory>> factory) {
		TradeOfferInternals.registerWanderingTraderOffers(level, factory);
	}

	/**
	 * Refreshes the trade list by resetting the trade lists to vanilla state, and then registering all trade offers again.
	 *
	 * <p>This method is geared for use by mods which for example provide data driven villager trades.
	 */
	public static void refreshOffers() {
		TradeOfferInternals.refreshOffers();
	}
}
