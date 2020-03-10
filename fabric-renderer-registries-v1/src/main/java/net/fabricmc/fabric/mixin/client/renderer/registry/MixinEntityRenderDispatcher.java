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

package net.fabricmc.fabric.mixin.client.renderer.registry;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ReloadableResourceManager;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
	@Shadow
	Map<EntityType<?>, EntityRenderer<?>> renderers;

	@Inject(method = "registerRenderers", at = @At(value = "RETURN"), require = 1)
	public void on_method_23167(ItemRenderer itemRenderer, ReloadableResourceManager manager, CallbackInfo info) {
		final EntityRenderDispatcher me = (EntityRenderDispatcher) (Object) this;
		EntityRendererRegistry.INSTANCE.initialize(me, me.textureManager, manager, itemRenderer, renderers);
	}
}
