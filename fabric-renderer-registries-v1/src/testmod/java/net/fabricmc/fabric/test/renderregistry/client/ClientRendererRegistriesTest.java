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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRendererRegistry;
import net.fabricmc.fabric.test.renderregistry.client.cooldown.HiddenCooldownProperties;
import net.fabricmc.fabric.test.renderregistry.client.cooldown.FlashingCooldownProperties;
import net.fabricmc.fabric.test.renderregistry.client.countlabel.ObfuscatedCountLabelProperties;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.DiscoBarProperties;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.EnergyBarProperties;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.ManaBarProperties;
import net.fabricmc.fabric.test.renderregistry.client.durabilitybar.WaterLavaBarProperties;
import net.fabricmc.fabric.test.renderregistry.common.RendererRegistriesTest;

public class ClientRendererRegistriesTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemOverlayRendererRegistry.setCountLabelProperties(RendererRegistriesTest.OBFUSCATED_COUNT, new ObfuscatedCountLabelProperties());

		ItemOverlayRendererRegistry.setDurabilityBarProperties(RendererRegistriesTest.ENERGY_STORAGE, new EnergyBarProperties());
		ItemOverlayRendererRegistry.setDurabilityBarProperties(RendererRegistriesTest.MANA_STORAGE, new ManaBarProperties());
		ItemOverlayRendererRegistry.setDurabilityBarProperties(RendererRegistriesTest.WATER_LAVA_BUCKET, new WaterLavaBarProperties());
		ItemOverlayRendererRegistry.setDurabilityBarProperties(RendererRegistriesTest.DISCO_BALL, new DiscoBarProperties());

		ItemOverlayRendererRegistry.setCooldownOverlayProperties(RendererRegistriesTest.LONG_COOLDOWN, new FlashingCooldownProperties());
		ItemOverlayRendererRegistry.setCooldownOverlayProperties(RendererRegistriesTest.HIDDEN_COOLDOWN, new HiddenCooldownProperties());
	}
}

