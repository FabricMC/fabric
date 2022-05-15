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

package net.fabricmc.fabric.impl.chat;

import net.fabricmc.fabric.api.chat.ChatDecoratorEvent;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *  Contains internals for chat decorators.
 */
public class ChatDecoratorInternals {
	/**
	 * Checks the cache and calls the chat decorator event.
	 * @param sender the sender of the message
	 * @param message the message
	 * @return the message from cache or {@code null} if there is none
	 */
	public static Text decorate(@Nullable ServerPlayerEntity sender, Text message) {
		// No caching for sender-less messages (e.g. commands)
		if (sender == null) return ChatDecoratorEvent.EVENT.invoker().decorate(null, message);
		PreviewCacheAccess cacheAccess = (PreviewCacheAccess)sender;
		String serializedOriginalText = cacheAccess.fabric_getSerializedOriginalText();
		// Messages are signed using sorted JSON serialization
		String serializedCurrentText = Text.Serializer.toSortedJsonString(message);
		Text cachedPreviewText = cacheAccess.fabric_getPreviewedText();
		// If there is no original text or if the two differs (null check included in equals)
		// cachedPreviewText null check is for safety, should not happen
		if (!Objects.equals(serializedOriginalText, serializedCurrentText) || cachedPreviewText == null) {
			// Store the current text. Note that this is usually called during preview phase.
			Text previewText = ChatDecoratorEvent.EVENT.invoker().decorate(sender, message);
			cacheAccess.fabric_setPreview(serializedCurrentText, previewText);
			return previewText;
		}
		// Cache hit
		return cachedPreviewText;
	}
}
