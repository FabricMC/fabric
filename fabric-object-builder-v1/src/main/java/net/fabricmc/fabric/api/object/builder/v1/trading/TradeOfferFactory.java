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

package net.fabricmc.fabric.api.object.builder.v1.trading;

import net.fabricmc.fabric.impl.object.builder.TradeOfferFactoryImpl;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

/**
 * Represents some generic {@link TradeOffers.Factory} implementations since the vanilla factories are package private classes.
 *
 * <p>The {@link TradeOffers.Factory}s generated are used in generating {@link TradeOffer}s for Villagers and Wandering Traders.
 */
public interface TradeOfferFactory {
	TradeOfferFactory INSTANCE = new TradeOfferFactoryImpl();

	/**
	 * Represents a trade where a villager buys an item and for one emerald, will "process" the item.
	 *
	 * <p>This specific overload of the method only takes one emerald to process the item</p>
	 *
	 * <p>Examples
	 * <ul>
	 * <li>6x Emeralds + 8x Raw park -> 8x Cooked pork
	 * <li>5x Emeralds + 1x Book -> 1x Enchanted Book (Note this requires further logic)
	 * </ul>
	 *
	 * @param buyItem The item to be processed
	 * @param buyCount The required amount of the item being processed.
	 * @param sellItem The item which is sold back to the player.
	 * @param sellCount The amount of the item sold back to the player.
	 * @param maxUses The amount of "uses" a trade can take before a villager needs to restock.
	 * @param experience The amount of experience the {@link VillagerEntity Villager} will receive.
	 * @return A new trade offer factory.
	 */
	default TradeOffers.Factory createProcessItemFactory(ItemConvertible buyItem, int buyCount, Item sellItem, int sellCount, int maxUses, int experience) {
		return this.createProcessItemFactory(buyItem, buyCount, 1, sellItem, sellCount, maxUses, experience);
	}

	/**
	 * Represents a trade where a villager buys an item and for a few emeralds, will "process" the item.
	 *
	 * <p>Examples
	 * <ul>
	 * <li>6x Emeralds + 8x Raw park -> 8x Cooked pork
	 * <li>5x Emeralds + 1x Book -> 1x Enchanted Book (Note this requires further logic for enchanting the book)
	 * </ul>
	 *
	 * @param buyItem The item to be processed.
	 * @param buyCount The required amount of the item being processed.
	 * @param emeraldPrice The required amount of emeralds.
	 * @param sellItem The item which is sold back to the player.
	 * @param sellCount The amount of the item sold back to the player.
	 * @param maxUses The amount of "uses" a trade can take before a villager needs to restock.
	 * @param experience The amount of experience the {@link VillagerEntity Villager} will receive.
	 * @return A new trade offer factory.
	 */
	TradeOffers.Factory createProcessItemFactory(ItemConvertible buyItem, int buyCount, int emeraldPrice, Item sellItem, int sellCount, int maxUses, int experience);

	/**
	 * Represents a trade offer where a villager will sell an item or amount of items for some emeralds.
	 *
	 * <p>Examples
	 * <ul>
	 * <li>3x Emeralds -> 2x White Banners
	 * <li>1x Emeralds -> 6x Cooked Chicken
	 * </ul>
	 *
	 * @param sellItem The item this villager will sell.
	 * @param emeraldPrice The amount of emeralds needed to buy this item.
	 * @param sellCount The amount of the item sold.
	 * @param maxUses The amount of "uses" a trade can take before a villager needs to restock.
	 * @param experience The amount of experience the {@link VillagerEntity Villager} will receive.
	 * @return A new trade offer factory.
	 */
	default TradeOffers.Factory createSellItemFactory(Item sellItem, int emeraldPrice, int sellCount, int maxUses, int experience) {
		return this.createSellItemFactory(new ItemStack(sellItem), emeraldPrice, sellCount, maxUses, experience);
	}

	/**
	 * Represents a trade offer where a villager will sell an item or amount of items for some emeralds.
	 *
	 * <p>Examples
	 * <ul>
	 * <li>3x Emeralds -> 2x White Banners
	 * <li>1x Emeralds -> 6x Cooked Chicken
	 * </ul>
	 *
	 * @param sellStack The item this villager will sell.
	 * @param emeraldPrice The amount of emeralds needed to buy this item.
	 * @param sellCount The amount of the item sold.
	 * @param maxUses The amount of "uses" a trade can take before a villager needs to restock.
	 * @param experience The amount of experience the {@link VillagerEntity Villager} will receive.
	 * @return A new trade offer factory.
	 */
	default TradeOffers.Factory createSellItemFactory(ItemStack sellStack, int emeraldPrice, int sellCount, int maxUses, int experience) {
		return this.createSellItemFactory(sellStack, emeraldPrice, sellCount, maxUses, experience, 0.05F);
	}

	/**
	 * Represents a trade offer where a villager will sell an item for amount of items for some emeralds.
	 *
	 * <p>Examples
	 * <ul>
	 * <li>3x Emeralds -> 2x White Banners
	 * <li>1x Emeralds -> 6x Cooked Chicken
	 * </ul>
	 *
	 * @param sellStack The item this villager will sell.
	 * @param emeraldPrice The amount of emeralds needed to buy this item.
	 * @param sellCount The amount of the item sold.
	 * @param maxUses The amount of "uses" a trade can take before a villager needs to restock.
	 * @param experience The amount of experience the {@link VillagerEntity Villager} will receive.
	 * @param multiplier A price multiplier which represents the rate of price growth when this offer is used.
	 * @return A new trade offer factory.
	 */
	TradeOffers.Factory createSellItemFactory(ItemStack sellStack, int emeraldPrice, int sellCount, int maxUses, int experience, float multiplier);

	/**
	 * Represents a trade offer where a villager buys an amount of an item for one emerald.
	 *
	 * <p>Examples
	 * <ul>
	 * <li>1x Oak Boat -> 1x Emerald
	 * </ul>
	 *
	 * @param buyItem The type of item the villager is buying.
	 * @param buyAmount The amount of items needed to receive one emerald.
	 * @param maxUses The amount of "uses" a trade can take before a villager needs to restock.
	 * @param experience The amount of experience the {@link VillagerEntity Villager} will receive.
	 * @return A new trade offer factory.
	 */
	TradeOffers.Factory createBuyItemForOneEmeraldFactory(ItemConvertible buyItem, int buyAmount, int maxUses, int experience);

	/**
	 * Turns a {@link TradeOffer} into a {@link TradeOffers.Factory}.
	 *
	 * <p>Since TradeOffers which currently belong to a villager are mutable, we serialize and then create a new offer to be wrapped.
	 *
	 * @param offer The offer to wrap.
	 * @return A new trade offer factory.
	 */
	TradeOffers.Factory wrap(TradeOffer offer);
}
