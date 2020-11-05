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

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_5617;
import net.minecraft.class_5619;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.impl.client.renderer.registry.RegistrationHelperImpl;
import net.fabricmc.fabric.impl.client.renderer.registry.EntityRendererRegistryImpl;

@Mixin(class_5619.class)
public abstract class MixinEntityRenderers {
	@Shadow()
	@Final
	private static Map<EntityType<?>, class_5617<?>> field_27768;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "<clinit>*", at = @At(value = "RETURN"))
	private static void onRegisterRenderers(CallbackInfo info) {
		EntityRendererRegistryImpl.setup(((t, factory) -> field_27768.put(t, factory)));
	}

	// synthetic lambda in method_32176
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Redirect(method = "method_32174", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5617;create(Lnet/minecraft/class_5617$class_5618;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
	private static EntityRenderer<?> createEntityRenderer(class_5617<?> entityRendererFactory, class_5617.class_5618 context, ImmutableMap.Builder builder, class_5617.class_5618 context2, EntityType<?> entityType) {
		EntityRenderer<?> entityRenderer = entityRendererFactory.create(context);

		if (entityRenderer instanceof LivingEntityRenderer) { // Must be living for features
			LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor) entityRenderer;
			LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers((EntityType<? extends LivingEntity>) entityType, (LivingEntityRenderer) entityRenderer, new RegistrationHelperImpl(accessor::callAddFeature), context);
		}

		return entityRenderer;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Redirect(method = "method_32175", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5617;create(Lnet/minecraft/class_5617$class_5618;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
	private static EntityRenderer<? extends PlayerEntity> createPlayerEntityRenderer(class_5617<AbstractClientPlayerEntity> playerEntityRendererFactory, class_5617.class_5618 context, ImmutableMap.Builder builder, class_5617.class_5618 context2, String str, class_5617<AbstractClientPlayerEntity> playerEntityRendererFactory2) {
		EntityRenderer<? extends PlayerEntity> entityRenderer = playerEntityRendererFactory.create(context);

		LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor) entityRenderer;
		LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers(EntityType.PLAYER, (LivingEntityRenderer) entityRenderer, new RegistrationHelperImpl(accessor::callAddFeature), context);

		return entityRenderer;
	}
}
