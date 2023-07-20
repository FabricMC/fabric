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

package net.fabricmc.fabric.test.renderer.client;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;

public class OctagonalColumnUnbakedModel implements UnbakedModel {
	private static final SpriteIdentifier WHITE_CONCRETE_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/white_concrete"));

	// (B - A) is the side length of a regular octagon that fits in a unit square.
	// The line from A to B is centered on the line from 0 to 1.
	private static final float A = (float) (1 - Math.sqrt(2) / 2);
	private static final float B = (float) (Math.sqrt(2) / 2);

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptySet();
	}

	@Override
	public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
	}

	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		if (!RendererAccess.INSTANCE.hasRenderer()) {
			return null;
		}

		Sprite whiteConcreteSprite = textureGetter.apply(WHITE_CONCRETE_SPRITE_ID);

		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		MaterialFinder finder = renderer.materialFinder();
		RenderMaterial glintMaterial = finder.glint(TriState.TRUE).find();

		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();

		// up

		emitter.pos(0, A, 1, 0);
		emitter.pos(1, 0.5f, 1, 0.5f);
		emitter.pos(2, 1, 1, A);
		emitter.pos(3, B, 1, 0);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		emitter.pos(0, 0, 1, A);
		emitter.pos(1, 0, 1, B);
		emitter.pos(2, 0.5f, 1, 0.5f);
		emitter.pos(3, A, 1, 0);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		emitter.pos(0, 0, 1, B);
		emitter.pos(1, A, 1, 1);
		emitter.pos(2, B, 1, 1);
		emitter.pos(3, 0.5f, 1, 0.5f);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		emitter.pos(0, 0.5f, 1, 0.5f);
		emitter.pos(1, B, 1, 1);
		emitter.pos(2, 1, 1, B);
		emitter.pos(3, 1, 1, A);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// down

		emitter.pos(0, A, 0, 1);
		emitter.pos(1, 0.5f, 0, 0.5f);
		emitter.pos(2, 1, 0, B);
		emitter.pos(3, B, 0, 1);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		emitter.pos(0, 0, 0, B);
		emitter.pos(1, 0, 0, A);
		emitter.pos(2, 0.5f, 0, 0.5f);
		emitter.pos(3, A, 0, 1);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		emitter.pos(0, 0, 0, A);
		emitter.pos(1, A, 0, 0);
		emitter.pos(2, B, 0, 0);
		emitter.pos(3, 0.5f, 0, 0.5f);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		emitter.pos(0, 0.5f, 0, 0.5f);
		emitter.pos(1, B, 0, 0);
		emitter.pos(2, 1, 0, A);
		emitter.pos(3, 1, 0, B);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// north
		emitter.pos(0, B, 1, 0);
		emitter.pos(1, B, 0, 0);
		emitter.pos(2, A, 0, 0);
		emitter.pos(3, A, 1, 0);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// northwest
		emitter.pos(0, A, 1, 0);
		emitter.pos(1, A, 0, 0);
		emitter.pos(2, 0, 0, A);
		emitter.pos(3, 0, 1, A);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// west
		emitter.pos(0, 0, 1, A);
		emitter.pos(1, 0, 0, A);
		emitter.pos(2, 0, 0, B);
		emitter.pos(3, 0, 1, B);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// southwest
		emitter.pos(0, 0, 1, B);
		emitter.pos(1, 0, 0, B);
		emitter.pos(2, A, 0, 1);
		emitter.pos(3, A, 1, 1);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// south
		emitter.pos(0, A, 1, 1);
		emitter.pos(1, A, 0, 1);
		emitter.pos(2, B, 0, 1);
		emitter.pos(3, B, 1, 1);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// southeast
		emitter.pos(0, B, 1, 1);
		emitter.pos(1, B, 0, 1);
		emitter.pos(2, 1, 0, B);
		emitter.pos(3, 1, 1, B);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// east
		emitter.pos(0, 1, 1, B);
		emitter.pos(1, 1, 0, B);
		emitter.pos(2, 1, 0, A);
		emitter.pos(3, 1, 1, A);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		// northeast
		emitter.pos(0, 1, 1, A);
		emitter.pos(1, 1, 0, A);
		emitter.pos(2, B, 0, 0);
		emitter.pos(3, B, 1, 0);
		emitter.spriteBake(whiteConcreteSprite, MutableQuadView.BAKE_LOCK_UV);
		emitter.material(glintMaterial);
		emitter.color(-1, -1, -1, -1);
		emitter.emit();

		return new SingleMeshBakedModel(builder.build(), whiteConcreteSprite);
	}
}
