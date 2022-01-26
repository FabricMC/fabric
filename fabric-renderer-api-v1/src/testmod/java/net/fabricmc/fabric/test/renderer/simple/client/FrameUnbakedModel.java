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

package net.fabricmc.fabric.test.renderer.simple.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

final class FrameUnbakedModel implements UnbakedModel {
	FrameUnbakedModel() {
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		return Collections.emptySet(); // TODO: Also set the return value when we set a proper texture.
	}

	/*
	 * Bake the model.
	 * In this case we can prebake the frame into a mesh, but will render the contained block when we draw the quads.
	 */
	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		// The renderer api may not have an implementation.
		// For this reason we will just null check the renderer impl
		if (RendererAccess.INSTANCE.hasRenderer()) {
			Renderer renderer = RendererAccess.INSTANCE.getRenderer();
			MeshBuilder builder = renderer.meshBuilder();
			QuadEmitter emitter = builder.getEmitter();
			// TODO: Just some random texture to get a missing texture, we should get a proper texture soon
			Sprite frameSprite = textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("foo:foo")));

			for (Direction direction : Direction.values()) {
				// Draw outer frame
				emitter.square(direction, 0.0F, 0.9F, 0.9F, 1.0F, 0.0F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				emitter.square(direction, 0.0F, 0.0F, 0.1F, 0.9F, 0.0F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				emitter.square(direction, 0.9F, 0.1F, 1.0F, 1.0F, 0.0F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				emitter.square(direction, 0.1F, 0.0F, 1.0F, 0.1F, 0.0F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				// Draw inner frame - inset by 0.9 so the frame looks like an actual mesh
				emitter.square(direction, 0.0F, 0.9F, 0.9F, 1.0F, 0.9F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				emitter.square(direction, 0.0F, 0.0F, 0.1F, 0.9F, 0.9F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				emitter.square(direction, 0.9F, 0.1F, 1.0F, 1.0F, 0.9F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();

				emitter.square(direction, 0.1F, 0.0F, 1.0F, 0.1F, 0.9F)
						.spriteBake(0, frameSprite, MutableQuadView.BAKE_LOCK_UV)
						.spriteColor(0, -1, -1, -1, -1)
						.emit();
			}

			return new FrameBakedModel(builder.build(), frameSprite);
		}

		// No renderer implementation is present.
		return null;
	}
}
