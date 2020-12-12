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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.registry.Registry;

class SoundButton extends ButtonWidget {
	SoundButton(int x, int y, int width, int height) {
		super(x, y, width, height, "Sound Button", null);
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(Registry.SOUND_EVENT.getRandom(ScreenTests.RANDOM), 1.0F, 1.0F));
	}
}
