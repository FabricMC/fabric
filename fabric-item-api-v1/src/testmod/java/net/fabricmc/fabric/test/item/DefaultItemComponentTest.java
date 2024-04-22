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

import java.util.List;

import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentsCallback;

public class DefaultItemComponentTest implements ModInitializer {
	@Override
	public void onInitialize() {
		DefaultItemComponentsCallback.MODIFY.register(context -> {
			context.modify(Items.GOLD_INGOT, builder -> {
				builder.add(DataComponentTypes.ITEM_NAME, Text.literal("Fool's Gold").formatted(Formatting.GOLD));
			});
			context.modify(Items.GOLD_NUGGET, builder -> {
				builder.add(DataComponentTypes.FIREWORKS, new FireworksComponent(1, List.of(
					new FireworkExplosionComponent(FireworkExplosionComponent.Type.STAR, IntList.of(0x32a852), IntList.of(0x32a852), true, true)
				)));
			});
		});

		// Make all fireworks glint
		DefaultItemComponentsCallback.AFTER_MODIFY.register(context -> {
			context.modify(DataComponentTypes.FIREWORKS, (fireworksComponent, builder) -> {
				builder.add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
			});
		});
	}
}
