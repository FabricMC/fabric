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

package net.fabricmc.indigo.renderer.mesh;

import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.EMPTY;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.HEADER_STRIDE;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.QUAD_STRIDE;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_COLOR;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_LIGHTMAP;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_NORMAL;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_STRIDE;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_U;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_X;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.indigo.renderer.IndigoRenderer;
import net.fabricmc.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.indigo.renderer.helper.ColorHelper.ShadeableQuad;
import net.fabricmc.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.indigo.renderer.helper.NormalHelper;
import net.fabricmc.indigo.renderer.helper.TextureHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Almost-concrete implementation of a mutable quad. The only missing part is {@link #emit()},
 * because that depends on where/how it is used. (Mesh encoding vs. render-time transformation).
 */
public abstract class MutableQuadViewImpl extends QuadViewImpl implements QuadEmitter, ShadeableQuad {
	public final void begin(int[] data, int baseIndex) {
		this.data = data;
		this.baseIndex = baseIndex;
		clear();
	}

	public void clear() {
		System.arraycopy(EMPTY, 0, data, baseIndex, EncodingFormat.TOTAL_STRIDE);
		isFaceNormalInvalid = true;
		isGeometryInvalid = true;
		normalFlags = 0;
		tag = 0;
		colorIndex = -1;
		cullFace = null;
		lightFace = null;
		nominalFace = null;
		material = IndigoRenderer.MATERIAL_STANDARD;
	}

	@Override
	public final MutableQuadViewImpl material(RenderMaterial material) {
		if (material == null || material.spriteDepth() > this.material.spriteDepth()) {
			throw new UnsupportedOperationException("Material texture depth must be the same or less than original material.");
		}
		this.material = (Value) material;
		return this;
	}

	@Override
	public final MutableQuadViewImpl cullFace(Direction face) {
		cullFace = face;
		nominalFace = face;
		return this;
	}

	public final MutableQuadViewImpl lightFace(Direction face) {
		lightFace = face;
		return this;
	}

	@Override
	public final MutableQuadViewImpl nominalFace(Direction face) {
		nominalFace = face;
		return this;
	}

	@Override
	public final MutableQuadViewImpl colorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
		return this;
	}

	@Override
	public final MutableQuadViewImpl tag(int tag) {
		this.tag = tag;
		return this;
	}

	@Override
	public final MutableQuadViewImpl fromVanilla(int[] quadData, int startIndex, boolean isItem) {
		System.arraycopy(quadData, startIndex, data, baseIndex + HEADER_STRIDE, QUAD_STRIDE);
		this.invalidateShape();
		return this;
	}

	@Override
	public boolean isFaceAligned() {
		return (geometryFlags() & GeometryHelper.AXIS_ALIGNED_FLAG) != 0;
	}

	@Override
	public boolean needsDiffuseShading(int textureIndex) {
		return textureIndex < material.spriteDepth() && !material.disableDiffuse(textureIndex);
	}

	@Override
	public MutableQuadViewImpl pos(int vertexIndex, float x, float y, float z) {
		final int index = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X;
		data[index] = Float.floatToRawIntBits(x);
		data[index + 1] = Float.floatToRawIntBits(y);
		data[index + 2] = Float.floatToRawIntBits(z);
		isFaceNormalInvalid = true;
		return this;
	}

	@Override
	public MutableQuadViewImpl normal(int vertexIndex, float x, float y, float z) {
		normalFlags |= (1 << vertexIndex);
		data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL] = NormalHelper.packNormal(x, y, z, 0);
		return this;
	}

	/**
	 * Internal helper method. Copies face normals to vertex normals lacking one.
	 */
	public final void populateMissingNormals() {
		final int normalFlags = this.normalFlags;
		if (normalFlags == 0b1111)
			return;
		final int packedFaceNormal = NormalHelper.packNormal(faceNormal(), 0);
		for (int v = 0; v < 4; v++) {
			if ((normalFlags & (1 << v)) == 0) {
				data[baseIndex + v * VERTEX_STRIDE + VERTEX_NORMAL] = packedFaceNormal;
			}
		}
	}

	@Override
	public MutableQuadViewImpl lightmap(int vertexIndex, int lightmap) {
		data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP] = lightmap;
		return this;
	}

	@Override
	public MutableQuadViewImpl spriteColor(int vertexIndex, int textureIndex, int color) {
		data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR] = color;
		return this;
	}

	@Override
	public MutableQuadViewImpl sprite(int vertexIndex, int textureIndex, float u, float v) {
		final int i = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U;
		data[i] = Float.floatToRawIntBits(u);
		data[i + 1] = Float.floatToRawIntBits(v);
		return this;
	}

	@Override
	public MutableQuadViewImpl spriteBake(int spriteIndex, Sprite sprite, int bakeFlags) {
		TextureHelper.bakeSprite(this, spriteIndex, sprite, bakeFlags);
		return this;
	}
}
