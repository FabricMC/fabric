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

package net.fabricmc.fabric.test.object.builder;

import static net.minecraft.command.argument.EntityArgumentType.entity;
import static net.minecraft.command.argument.EntityArgumentType.getEntity;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

public class VillagerTypeTest1 implements ModInitializer {
	@Override
	public void onInitialize() {
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 1, factories -> {
			factories.add(new SimpleTradeFactory(new TradeOffer(new ItemStack(Items.GOLD_INGOT, 3), new ItemStack(Items.NETHERITE_SCRAP, 4), new ItemStack(Items.NETHERITE_INGOT), 2, 6, 0.15F)));
		});

		TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
			factories.add(new SimpleTradeFactory(new TradeOffer(new ItemStack(Items.GOLD_INGOT, 3), new ItemStack(Items.NETHERITE_SCRAP, 4), new ItemStack(Items.NETHERITE_INGOT), 2, 6, 0.35F)));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("fabric_applywandering_trades")
					.then(argument("entity", entity()).executes(context -> {
						final Entity entity = getEntity(context, "entity");

						if (!(entity instanceof WanderingTraderEntity)) {
							throw new SimpleCommandExceptionType(Text.literal("Entity is not a wandering trader")).create();
						}

						WanderingTraderEntity trader = (WanderingTraderEntity) entity;
						trader.getOffers().clear();

						for (TradeOffers.Factory[] value : TradeOffers.WANDERING_TRADER_TRADES.values()) {
							for (TradeOffers.Factory factory : value) {
								final TradeOffer result = factory.create(trader, Random.create());

								if (result == null) {
									continue;
								}

								trader.getOffers().add(result);
							}
						}

						return 1;
					})));
		});
	}
}
