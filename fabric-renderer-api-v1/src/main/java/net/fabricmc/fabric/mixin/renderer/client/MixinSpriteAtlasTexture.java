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

package net.fabricmc.fabric.mixin.renderer.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.renderer.SpriteFinderImpl;

@Mixin(SpriteAtlasTexture.class)
public class MixinSpriteAtlasTexture implements SpriteFinderImpl.SpriteFinderAccess {
	@Shadow
	private Map<Identifier, Sprite> sprites;

	private SpriteFinderImpl fabric_spriteFinder = null;

	@Inject(at = @At("RETURN"), method = "upload")
	private void uploadHook(SpriteAtlasTexture.Data input, CallbackInfo info) {
		fabric_spriteFinder = null;
	}

	@Override
	public SpriteFinderImpl fabric_spriteFinder() {
		SpriteFinderImpl result = fabric_spriteFinder;

		if (result == null) {
			result = new SpriteFinderImpl(sprites);
			fabric_spriteFinder = result;
		}

		return result;
	}
}
