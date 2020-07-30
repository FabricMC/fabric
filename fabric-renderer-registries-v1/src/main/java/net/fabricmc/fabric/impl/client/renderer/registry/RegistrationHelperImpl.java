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

package net.fabricmc.fabric.impl.client.renderer.registry;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;

public final class RegistrationHelperImpl implements LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper {
	private final Function<FeatureRenderer<?, ?>, Boolean> delegate;

	public RegistrationHelperImpl(Function<FeatureRenderer<?, ?>, Boolean> delegate) {
		this.delegate = delegate;
	}

	@Override
	public <T extends LivingEntity> void register(FeatureRenderer<T, ? extends EntityModel<T>> featureRenderer) {
		Objects.requireNonNull(featureRenderer, "Feature renderer cannot be null");
		this.delegate.apply(featureRenderer);
	}
}

