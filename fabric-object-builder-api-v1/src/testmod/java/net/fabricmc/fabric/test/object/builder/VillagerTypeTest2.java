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

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

/*
 * Second entrypoint to validate class loading does not break this.
 */
public class VillagerTypeTest2 implements ModInitializer {
	@Override
	public void onInitialize() {
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 1, factories -> {
			factories.add(new SimpleTradeFactory(new TradeOffer(new ItemStack(Items.DIAMOND, 20), new ItemStack(Items.NETHERITE_INGOT), 3, 4, 0.15F)));
		});
	}
}
