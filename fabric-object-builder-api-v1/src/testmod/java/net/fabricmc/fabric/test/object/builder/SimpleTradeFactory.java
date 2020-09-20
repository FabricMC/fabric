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

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

class SimpleTradeFactory implements TradeOffers.Factory {
	private final TradeOffer offer;

	SimpleTradeFactory(TradeOffer offer) {
		this.offer = offer;
	}

	@Override
	public TradeOffer create(Entity entity, Random random) {
		// ALWAYS supply a copy of the offer.
		return new TradeOffer(this.offer.toTag());
	}
}
