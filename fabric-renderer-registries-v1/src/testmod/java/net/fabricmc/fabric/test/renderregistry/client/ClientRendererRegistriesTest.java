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

package net.fabricmc.fabric.test.renderregistry.client;

import net.minecraft.util.Formatting;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRendererRegistry;
import net.fabricmc.fabric.test.renderregistry.client.cooldown.HiddenCooldownInfo;
import net.fabricmc.fabric.test.renderregistry.client.cooldown.FlashingCooldownInfo;
import net.fabricmc.fabric.test.renderregistry.client.countlabel.ObfuscatedItemLabelInfo;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.DiscoBarInfo;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.EnergyBarInfo;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.ManaBarInfo;
import net.fabricmc.fabric.test.renderregistry.client.postrender.WarningIcon;
import net.fabricmc.fabric.test.renderregistry.client.prerender.StackBorder;
import net.fabricmc.fabric.test.renderregistry.common.RendererRegistriesTest;

public class ClientRendererRegistriesTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemOverlayRendererRegistry.setLabelInfo(RendererRegistriesTest.OBFUSCATED_COUNT, new ObfuscatedItemLabelInfo());

		ItemOverlayRendererRegistry.setDamageBarInfo(RendererRegistriesTest.ENERGY_STORAGE, new EnergyBarInfo());
		ItemOverlayRendererRegistry.setDamageBarInfo(RendererRegistriesTest.MANA_STORAGE, new ManaBarInfo());
		ItemOverlayRendererRegistry.setDamageBarInfo(RendererRegistriesTest.DISCO_BALL, new DiscoBarInfo());

		ItemOverlayRendererRegistry.setCooldownInfo(RendererRegistriesTest.LONG_COOLDOWN, new FlashingCooldownInfo());
		ItemOverlayRendererRegistry.setCooldownInfo(RendererRegistriesTest.HIDDEN_COOLDOWN, new HiddenCooldownInfo());

		ItemOverlayRendererRegistry.setPreRenderer(RendererRegistriesTest.TUNISIAN_DIAMOND, new StackBorder(Formatting.GOLD));
		ItemOverlayRendererRegistry.setPreRenderer(RendererRegistriesTest.MYSTERIOUS_BOOK, new StackBorder(Formatting.DARK_PURPLE));
		ItemOverlayRendererRegistry.setPostRenderer(RendererRegistriesTest.MYSTERIOUS_BOOK, new WarningIcon());
	}
}

