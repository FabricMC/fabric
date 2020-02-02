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

import net.fabricmc.fabric.api.object.builder.v1.trading.TradeOfferFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

public class TradeOfferFactoryImpl implements TradeOfferFactory {
	@Override
	public TradeOffers.Factory createProcessItemFactory(ItemConvertible buyItem, int buyCount, int emeraldPrice, Item sellItem, int sellCount, int maxUses, int experience) {
		return (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, emeraldPrice), new ItemStack(buyItem, buyCount), new ItemStack(sellItem, sellCount), maxUses, experience, 0.05F);
	}

	@Override
	public TradeOffers.Factory createSellItemFactory(ItemStack sellStack, int emeraldPrice, int sellCount, int maxUses, int experience, float multiplier) {
		return (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, emeraldPrice), new ItemStack(sellStack.getItem(), sellCount), maxUses, experience, multiplier);
	}

	@Override
	public TradeOffers.Factory createBuyItemForOneEmeraldFactory(ItemConvertible buyItem, int buyAmount, int maxUses, int experience) {
		return (entity, random) -> new TradeOffer(new ItemStack(buyItem, buyAmount), new ItemStack(Items.EMERALD), maxUses, experience, 0.05F);
	}

	@Override
	public TradeOffers.Factory wrap(TradeOffer offer) {
		CompoundTag tag = offer.toTag();
		return (entity, random) -> new TradeOffer(tag);
	}
}
