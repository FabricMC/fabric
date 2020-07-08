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

package net.fabricmc.fabric.api.client.rendering.v1;

import java.util.function.Consumer;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Called when {@link FeatureRenderer feature renderers} for a {@link LivingEntityRenderer living entity renderer} are registered.
 *
 * <p>Feature renderers are typically used for rendering additional objects on an entity, such as armor, an elytra or {@link Deadmau5FeatureRenderer Deadmau5's ears}.
 * This callback lets developers add additional feature renderers for use in entity rendering.
 * Listeners should filter out the specific entity renderer they want to hook into, usually through {@code instanceof} checks.
 * Once listeners find a suitable entity renderer, they should register their feature renderer via the acceptor.
 *
 * <p>For example, to register a feature renderer for a player model, the example below may used:
 * <blockquote><pre>
 * RegisterFeatureRendererCallback.EVENT.register((entityRenderer, acceptor) -> {
 * 	if (entityRenderer instanceof PlayerEntityModel) {
 * 		acceptor.accept(new MyFeatureRenderer((PlayerEntityModel) entityRenderer));
 * 	}
 * });
 * </pre></blockquote>
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface RegisterFeatureRendererCallback {
	Event<RegisterFeatureRendererCallback> EVENT = EventFactory.createArrayBacked(RegisterFeatureRendererCallback.class, callbacks -> (entityRenderer, acceptor) -> {
		for (RegisterFeatureRendererCallback callback : callbacks) {
			callback.registerFeatureRenderers(entityRenderer, acceptor);
		}
	});

	void registerFeatureRenderers(LivingEntityRenderer<?, ?> entityRenderer, Consumer<FeatureRenderer<?, ?>> acceptor);
}
