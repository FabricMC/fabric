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

package net.fabricmc.fabric.test.renderer.registry.client;

import net.minecraft.util.Formatting;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRendererRegistry;
import net.fabricmc.fabric.test.renderer.registry.client.cooldown.HiddenCooldownOverlayInfo;
import net.fabricmc.fabric.test.renderer.registry.client.cooldown.FlashingCooldownOverlayInfo;
import net.fabricmc.fabric.test.renderer.registry.client.countlabel.ObfuscatedItemLabelInfo;
import net.fabricmc.fabric.test.renderer.registry.client.durabilitybar.DiscoBarInfo;
import net.fabricmc.fabric.test.renderer.registry.client.durabilitybar.EnergyBarInfo;
import net.fabricmc.fabric.test.renderer.registry.client.durabilitybar.ManaBarInfo;
import net.fabricmc.fabric.test.renderer.registry.client.postrender.WarningIcon;
import net.fabricmc.fabric.test.renderer.registry.client.prerender.StackBorder;
import net.fabricmc.fabric.test.renderer.registry.common.CustomOverlayItemsTest;

public final class ItemOverlayRendererTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemOverlayRendererRegistry.setLabelInfo(CustomOverlayItemsTest.OBFUSCATED_COUNT, new ObfuscatedItemLabelInfo());

		ItemOverlayRendererRegistry.setDamageBarInfo(CustomOverlayItemsTest.ENERGY_STORAGE, new EnergyBarInfo());
		ItemOverlayRendererRegistry.setDamageBarInfo(CustomOverlayItemsTest.MANA_STORAGE, new ManaBarInfo());
		ItemOverlayRendererRegistry.setDamageBarInfo(CustomOverlayItemsTest.DISCO_BALL, new DiscoBarInfo());

		ItemOverlayRendererRegistry.setCooldownOverlayInfo(CustomOverlayItemsTest.LONG_COOLDOWN, new FlashingCooldownOverlayInfo());
		ItemOverlayRendererRegistry.setCooldownOverlayInfo(CustomOverlayItemsTest.HIDDEN_COOLDOWN, new HiddenCooldownOverlayInfo());

		ItemOverlayRendererRegistry.setPreRenderer(CustomOverlayItemsTest.TUNISIAN_DIAMOND, new StackBorder(Formatting.GOLD));
		ItemOverlayRendererRegistry.setPreRenderer(CustomOverlayItemsTest.MYSTERIOUS_BOOK, new StackBorder(Formatting.DARK_PURPLE));
		ItemOverlayRendererRegistry.setPostRenderer(CustomOverlayItemsTest.MYSTERIOUS_BOOK, new WarningIcon());
	}
}

