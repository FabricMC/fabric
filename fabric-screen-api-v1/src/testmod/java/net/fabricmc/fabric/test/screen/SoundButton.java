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

package net.fabricmc.fabric.test.screen;

import java.util.Random;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

class SoundButton extends PressableWidget {
	private static final Random RANDOM = new Random();

	SoundButton(int x, int y, int width, int height) {
		super(x, y, width, height, Text.of("Sound Button"));
	}

	@Override
	public void onPress() {
		final SoundEvent event = Registry.SOUND_EVENT.getRandom(RANDOM).map(RegistryEntry::value).orElse(SoundEvents.ENTITY_GENERIC_EXPLODE);
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, 1.0F, 1.0F));
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder arg) {
	}
}
