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

package net.fabricmc.fabric.impl.renderer;

import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;

/**
 * Indexes an atlas sprite to allow fast lookup of Sprites from
 * baked vertex coordinates.  Implementation is a straightforward
 * quad tree. Other options that were considered were linear search
 * (slow) and direct indexing of fixed-size cells. Direct indexing
 * would be fastest but would be memory-intensive for large atlases
 * and unsuitable for any atlas that isn't consistently aligned to
 * a fixed cell size.
 */
public class SpriteFinderImpl implements SpriteFinder {
	private final Node root;

	public SpriteFinderImpl(Map<Identifier, Sprite> sprites) {
		root = new Node(0.5f, 0.5f, 0.25f);
		sprites.values().forEach(root::add);
	}

	@Override
	public Sprite find(QuadView quad, int textureIndex) {
		float u = 0;
		float v = 0;

		for (int i = 0; i < 4; i++) {
			u += quad.spriteU(i, textureIndex);
			v += quad.spriteV(i, textureIndex);
		}

		return find(u * 0.25f, v * 0.25f);
	}

	@Override
	public Sprite find(float u, float v) {
		return root.find(u, v);
	}

	private static class Node {
		final float midU;
		final float midV;
		final float cellRadius;
		Object lowLow = null;
		Object lowHigh = null;
		Object highLow = null;
		Object highHigh = null;

		Node(float midU, float midV, float radius) {
			this.midU = midU;
			this.midV = midV;
			this.cellRadius = radius;
		}

		static final float EPS = 0.00001f;

		void add(Sprite sprite) {
			final boolean lowU = sprite.getMinU() < midU - EPS;
			final boolean highU = sprite.getMaxU() > midU + EPS;
			final boolean lowV = sprite.getMinV() < midV - EPS;
			final boolean highV = sprite.getMaxV() > midV + EPS;

			if (lowU && lowV) {
				addInner(sprite, lowLow, -1, -1, q -> lowLow = q);
			}

			if (lowU && highV) {
				addInner(sprite, lowHigh, -1, 1, q -> lowHigh = q);
			}

			if (highU && lowV) {
				addInner(sprite, highLow, 1, -1, q -> highLow = q);
			}

			if (highU && highV) {
				addInner(sprite, highHigh, 1, 1, q -> highHigh = q);
			}
		}

		private void addInner(Sprite sprite, Object quadrant, int uStep, int vStep, Consumer<Object> setter) {
			if (quadrant == null) {
				setter.accept(sprite);
			} else if (quadrant instanceof Node) {
				((Node) quadrant).add(sprite);
			} else {
				Node n = new Node(midU + cellRadius * uStep, midV + cellRadius * vStep, cellRadius * 0.5f);

				if (quadrant instanceof Sprite) {
					n.add((Sprite) quadrant);
				}

				n.add(sprite);
				setter.accept(n);
			}
		}

		private Sprite find(float u, float v) {
			if (u < midU) {
				return v < midV ? findInner(lowLow, u, v) : findInner(lowHigh, u, v);
			} else {
				return v < midV ? findInner(highLow, u, v) : findInner(highHigh, u, v);
			}
		}

		private Sprite findInner(Object quadrant, float u, float v) {
			if (quadrant instanceof Sprite) {
				return (Sprite) quadrant;
			} else if (quadrant instanceof Node) {
				return ((Node) quadrant).find(u, v);
			} else {
				return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(MissingSprite.getMissingSpriteId());
			}
		}
	}

	public static SpriteFinderImpl get(SpriteAtlasTexture atlas) {
		return ((SpriteFinderAccess) atlas).fabric_spriteFinder();
	}

	public interface SpriteFinderAccess {
		SpriteFinderImpl fabric_spriteFinder();
	}
}
