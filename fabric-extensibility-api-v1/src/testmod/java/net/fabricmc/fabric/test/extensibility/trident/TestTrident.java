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

package net.fabricmc.fabric.test.extensibility.trident;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.extensibility.item.v1.trident.SimpleTridentItem;
import net.fabricmc.fabric.api.extensibility.item.v1.trident.SimpleTridentItemEntity;

/**
 * This trident burns entities when thrown at them.
 */
public class TestTrident extends SimpleTridentItem {
	public TestTrident(Item.Settings settings) {
		super(settings, new Identifier("fabric-extensibility-api-v1-testmod", "textures/entity/test_trident.png"));
	}

	@Override
	public TridentEntity modifyTridentEntity(TridentEntity trident) {
		return new SimpleTridentItemEntity(trident) {
			@Override
			protected void onHit(LivingEntity target) {
				super.onHit(target);
				target.setOnFireFor(5);
			}
		};
	}
}
