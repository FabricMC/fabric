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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ReloadableResourceManager;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.impl.client.renderer.registry.RegistrationHelperImpl;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
	@Shadow
	@Final
	private Map<EntityType<?>, EntityRenderer<?>> renderers;

	@Shadow
	@Final
	private Map<String, PlayerEntityRenderer> modelRenderers;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "registerRenderers", at = @At(value = "TAIL"))
	public void onRegisterRenderers(ItemRenderer itemRenderer, ReloadableResourceManager manager, CallbackInfo info) {
		final EntityRenderDispatcher me = (EntityRenderDispatcher) (Object) this;
		EntityRendererRegistry.INSTANCE.initialize(me, me.textureManager, manager, itemRenderer, renderers);

		// Dispatch events to register feature renderers.
		for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : this.renderers.entrySet()) {
			if (entry.getValue() instanceof LivingEntityRenderer) { // Must be living for features
				LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor) entry.getValue();

				LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers((EntityType<? extends LivingEntity>) entry.getKey(), (LivingEntityRenderer) entry.getValue(), new RegistrationHelperImpl(accessor::callAddFeature));
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterRegisterPlayerModels(CallbackInfo ci) {
		// Players are a fun case, we need to do these separately and per model type
		for (Map.Entry<String, PlayerEntityRenderer> entry : this.modelRenderers.entrySet()) {
			LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor) entry.getValue();

			LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers(EntityType.PLAYER, entry.getValue(), new RegistrationHelperImpl(accessor::callAddFeature));
		}
	}
}
