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

package net.fabricmc.fabric.api.client.model.loading.v1;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

/**
 * An unbaked model that returns another {@link BakedModel} at {@linkplain #bake bake time}.
 * This allows multiple {@link UnbakedModel}s to share the same {@link BakedModel} instance
 * and prevents baking the same model multiple times.
 */
public final class DelegatingUnbakedModel implements UnbakedModel {
	private final Identifier delegate;

	/**
	 * Constructs a new delegating model.
	 *
	 * @param delegate The identifier of the underlying baked model.
	 */
	public DelegatingUnbakedModel(Identifier delegate) {
		this.delegate = delegate;
	}

	@Override
	public void resolve(Resolver resolver) {
		resolver.resolve(delegate);
	}

	@Override
	@Nullable
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
		return baker.bake(delegate, rotationContainer);
	}
}
