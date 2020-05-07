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

package net.fabricmc.fabric.test.event.entity.tick;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.v1.EntityTickCallback;

public class EntityTickEventTest implements ModInitializer {
	private static void glowingTickTest(LivingEntity entity) {
		if (!entity.getEntityWorld().isClient()) { // This test should only run on a server world
			if (entity.getMainHandStack().getItem() == Items.GLOWSTONE_DUST) {
				entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2)); // Duration has to be 2 or else the entity will never actually glow
			}
		}
	}

	private static void slowWhileUsingShield(PlayerEntity player) {
		if (!player.getEntityWorld().isClient()) { // This test should only run on a server world
			if (player.isBlocking()) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 2, 2));
			}
		}
	}

	@Override
	public void onInitialize() {
		// When a living entity is holding glowstone dust, apply the glow effect.
		EntityTickCallback.event(LivingEntity.class).register(EntityTickEventTest::glowingTickTest);
		// When a player is blocking with a shield, apply slowness 2.
		EntityTickCallback.event(PlayerEntity.class).register(EntityTickEventTest::slowWhileUsingShield);
	}
}
