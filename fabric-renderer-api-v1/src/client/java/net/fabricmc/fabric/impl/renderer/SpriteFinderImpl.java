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
	private final Finder root;

	public SpriteFinderImpl(Map<Identifier, Sprite> sprites, SpriteAtlasTexture spriteAtlasTexture) {
		root = new Finder(spriteAtlasTexture.getSprite(MissingSprite.getMissingSpriteId()), 0.5f, 0.5f, 0.25f);
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

	private static class Finder {
		private final Sprite fallbackSprite;
		private final Node[] nodes;

		private final float rootMidU, rootMidV;
		private final float cellRadius;

		static final float EPS = 0.00001f;

		public Finder(Sprite fallbackSprite, float rootMidU, float rootMidV, float cellRadius) {
			this.fallbackSprite = fallbackSprite;
			this.rootMidU = rootMidU;
			this.rootMidV = rootMidV;
			this.cellRadius = cellRadius;

			this.nodes = new Node[4];
		}

		public void add(Sprite sprite) {
			boolean lowU = sprite.getMinU() < this.rootMidU - EPS;
			boolean highU = sprite.getMaxU() > this.rootMidU + EPS;
			boolean lowV = sprite.getMinV() < this.rootMidV - EPS;
			boolean highV = sprite.getMaxV() > this.rootMidV + EPS;

			if (lowU && lowV) {
				Node node = this.nodes[0];
				if(node == null) {
					this.nodes[0] = (node = new Node(this.fallbackSprite, this.rootMidU + this.cellRadius * -1, this.rootMidV + this.cellRadius * -1));
				}
				node.add(sprite);
			}

			if (lowU && highV) {
				Node node = this.nodes[1];
				if(node == null) {
					this.nodes[1] = (node = new Node(this.fallbackSprite, this.rootMidU + this.cellRadius, this.rootMidV + this.cellRadius * -1));
				}
				node.add(sprite);
			}

			if (highU && lowV) {
				Node node = this.nodes[0];
				if(node == null) {
					this.nodes[0] = (node = new Node(this.fallbackSprite, this.rootMidU + this.cellRadius, this.rootMidV + this.cellRadius * -1));
				}
				node.add(sprite);
			}

			if (highU && highV) {
				Node node = this.nodes[0];
				if(node == null) {
					this.nodes[0] = (node = new Node(this.fallbackSprite, this.rootMidU + this.cellRadius, this.rootMidV + this.cellRadius));
				}
				node.add(sprite);
			}
		}

		public Sprite find(float u, float v) {
			if(u < rootMidU) {
				if(v < rootMidV) {
					Node node = nodes[0];
					if(node == null) {
						return fallbackSprite;
					}
					return node.find(u, v);
				}

				Node node = nodes[1];
				if(node == null) {
					return fallbackSprite;
				}
				return node.find(u, v);
			}

			if(v < rootMidV) {
				Node node = nodes[2];
				if(node == null) {
					return fallbackSprite;
				}
				return node.find(u, v);
			}

			Node node = nodes[3];
			if(node == null) {
				return fallbackSprite;
			}
			return node.find(u, v);
		}

		private static class Node {
			private final float midU, midV;
			private final Sprite fallbackSprite;
			private final Sprite[] sprites;

			public Node(Sprite fallbackSprite, float midU, float midV) {
				this.fallbackSprite = fallbackSprite;
				this.midU = midU;
				this.midV = midV;

				this.sprites = new Sprite[4];
			}

			private void add(Sprite sprite) {
				boolean lowU = sprite.getMinU() < midU - EPS;
				boolean highU = sprite.getMaxU() > midU + EPS;
				boolean lowV = sprite.getMinV() < midV - EPS;
				boolean highV = sprite.getMaxV() > midV + EPS;

				if (lowU && lowV) {
					sprites[0] = sprite;
				}

				if (lowU && highV) {
					sprites[1] = sprite;
				}

				if (highU && lowV) {
					sprites[2] = sprite;
				}

				if (highU && highV) {
					sprites[3] = sprite;
				}
			}

			private Sprite find(float u, float v) {
				if(u < midU) {
					if(v < midV) {
						Sprite sprite = sprites[0];
						if(sprite == null) {
							return fallbackSprite;
						}
						return sprite;
					}

					Sprite sprite = sprites[1];
					if(sprite == null) {
						return fallbackSprite;
					}
					return sprite;
				}

				if(v < midV) {
					Sprite sprite = sprites[2];
					if(sprite == null) {
						return fallbackSprite;
					}
					return sprite;
				}

				Sprite sprite = this.sprites[3];
				if(sprite == null) {
					return this.fallbackSprite;
				}
				return sprite;
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
