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

package net.fabricmc.fabric.test.item;

import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import net.fabricmc.api.ModInitializer;

public class ItemUpdateAnimationTest implements ModInitializer {
	public static final ComponentType<Integer> TICKS = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("fabric-item-api-v1-testmod", "ticks"),
																			ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());

	@Override
	public void onInitialize() {
		RegistryKey<Item> updatingAllowedKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("fabric-item-api-v1-testmod", "updating_allowed"));
		RegistryKey<Item> updatingDisallowedKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("fabric-item-api-v1-testmod", "updating_disallowed"));

		Registry.register(Registries.ITEM, updatingAllowedKey, new UpdatingItem(true, new Item.Settings().registryKey(updatingAllowedKey)));
		Registry.register(Registries.ITEM, updatingDisallowedKey, new UpdatingItem(false, new Item.Settings().registryKey(updatingDisallowedKey)));
	}
}
