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

package net.fabricmc.fabric.test.chat;

import net.minecraft.text.Text;
import net.minecraft.util.math.random.AbstractRandom;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.chat.v1.ChatDecoratorEvent;

public class ChatTest implements ModInitializer {
	@Override
	public void onInitialize() {
		AbstractRandom random = AbstractRandom.create();

		// Basic content phase testing
		ChatDecoratorEvent.EVENT.register(ChatDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
			if (message.getString().contains("tater")) {
				return message.copy().append(" :tiny_potato:");
			}

			return message;
		});

		// Basic styling phase testing
		ChatDecoratorEvent.EVENT.register(ChatDecoratorEvent.STYLING_PHASE, (sender, message) -> {
			if (sender != null && sender.getAbilities().creativeMode) {
				return message.copy().styled(style -> style.withColor(0xFFA500));
			}

			return message;
		});

		// Test whether caching works
		// Make sure that the message is sent with the previewed content,
		// and that the previewed content changes after typing other characters.
		ChatDecoratorEvent.EVENT.register(ChatDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
			if (message.getString().startsWith("random")) {
				return Text.of(Integer.toString(random.nextInt(100)));
			}

			return message;
		});
	}
}
