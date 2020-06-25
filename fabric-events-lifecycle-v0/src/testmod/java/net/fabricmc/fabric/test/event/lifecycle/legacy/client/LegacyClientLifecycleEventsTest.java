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

package net.fabricmc.fabric.test.event.lifecycle.legacy.client;

import net.minecraft.text.LiteralText;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.fabricmc.fabric.test.event.lifecycle.legacy.LegacyLifecycleEventsTest;

public class LegacyClientLifecycleEventsTest implements ClientModInitializer {
	private int ticks;

	@Override
	public void onInitializeClient() {
		ClientTickCallback.EVENT.register(client -> {
			this.ticks++; // Just track our own tick since the client doesn't have a ticks value.

			if (this.ticks % 200 == 0) {
				LegacyLifecycleEventsTest.LOGGER.info("Ticked Client at " + this.ticks + " ticks. (Legacy)");
			}
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			lines.add(new LiteralText("A Legacy Tooltip"));
		});
	}
}
