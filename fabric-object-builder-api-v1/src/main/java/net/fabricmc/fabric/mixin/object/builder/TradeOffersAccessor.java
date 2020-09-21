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

package net.fabricmc.fabric.mixin.object.builder;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

@Mixin(TradeOffers.class)
public interface TradeOffersAccessor {
	@Accessor("PROFESSION_TO_LEVELED_TRADE")
	static void setVillagerTradeMap(Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> trades) {
		throw new AssertionError("This should not happen!");
	}

	@Accessor("WANDERING_TRADER_TRADES")
	static void setWanderingTraderTradeMap(Int2ObjectMap<TradeOffers.Factory[]> trades) {
		throw new AssertionError("This should not happen!");
	}
}
