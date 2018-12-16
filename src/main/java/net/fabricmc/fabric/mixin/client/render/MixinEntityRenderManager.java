/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.client.render;

import net.fabricmc.fabric.client.render.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderManager {
	@Shadow
	private Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers;

	@Inject(method = "<init>(Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/client/render/item/ItemRenderer;Lnet/minecraft/resource/ReloadableResourceManager;)V", at = @At("RETURN"), require = 0)
	public void init(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager manager, CallbackInfo info) {
		EntityRendererRegistry.INSTANCE.initialize((EntityRenderDispatcher) (Object) this, textureManager, manager, itemRenderer, renderers);
	}
}
