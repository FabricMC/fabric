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

package net.fabricmc.fabric.test.entity.event.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.entity.event.client.ClientPlayerEvents;
import net.fabricmc.fabric.test.entity.event.EntityEventTests;

public class EntityEventTestsClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityEventTestsClient.class);

	@Override
	public void onInitializeClient() {
		LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(player -> {
			return !player.getEquippedStack(EquipmentSlot.CHEST).isOf(EntityEventTests.DIAMOND_ELYTRA);
		});

		ClientPlayerEvents.MODIFY_PLAYER_MOVEMENT_DURING_USINGITEM.register(player -> {
			LOGGER.info("Player {} is moving during using item.", player);

			if (player.getMainHandStack().isOf(Items.BOW) && player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.DIAMOND_BOOTS)) {
				LOGGER.info("Player {} can move without slowdown becase of diamond boots on feet.", player);
				player.input.movementForward *= 5F;
				player.input.movementSideways *= 5F;
			}
		});
	}
}
