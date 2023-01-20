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

package net.fabricmc.fabric.mixin.object.builder.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {
	@Shadow
	@Final
	public static Identifier SIGNS_ATLAS_TEXTURE;

	@Inject(method = "createSignTextureId", at = @At("HEAD"), cancellable = true)
	private static void modifyTextureId(SignType type, CallbackInfoReturnable<SpriteIdentifier> cir) {
		if (type.getName().indexOf(Identifier.NAMESPACE_SEPARATOR) != -1) {
			Identifier identifier = new Identifier(type.getName());
			cir.setReturnValue(new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new Identifier(identifier.getNamespace(), "entity/signs/" + identifier.getPath())));
		}
	}
}
