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

package net.fabricmc.fabric.mixin.chat;

import net.fabricmc.fabric.impl.chat.PreviewCacheAccess;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements PreviewCacheAccess {
	@Unique
	@Nullable String serializedOriginalText;

	@Unique
	@Nullable Text previewedText;

	@Override
	public @Nullable String fabric_getSerializedOriginalText() {
		return this.serializedOriginalText;
	}

	@Override
	public @Nullable Text fabric_getPreviewedText() {
		return this.previewedText;
	}

	@Override
	public void fabric_setPreview(@Nullable String serializedOriginalText, @Nullable Text previewedText) {
		this.serializedOriginalText = serializedOriginalText;
		this.previewedText = previewedText;
	}
}
