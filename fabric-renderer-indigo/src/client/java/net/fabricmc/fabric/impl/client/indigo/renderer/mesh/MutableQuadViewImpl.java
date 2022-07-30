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

package net.fabricmc.fabric.impl.client.indigo.renderer.mesh;

import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.EMPTY;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_BITS;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_COLOR_INDEX;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_TAG;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.QUAD_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_COLOR;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_LIGHTMAP;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_NORMAL;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_U;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_X;

import com.google.common.base.Preconditions;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.NormalHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.TextureHelper;

/**
 * Almost-concrete implementation of a mutable quad. The only missing part is {@link #emit()},
 * because that depends on where/how it is used. (Mesh encoding vs. render-time transformation).
 */
public abstract class MutableQuadViewImpl extends QuadViewImpl implements QuadEmitter {
	public final void begin(int[] data, int baseIndex) {
		this.data = data;
		this.baseIndex = baseIndex;
		clear();
	}

	public void clear() {
		System.arraycopy(EMPTY, 0, data, baseIndex, EncodingFormat.TOTAL_STRIDE);
		isGeometryInvalid = true;
		nominalFace = null;
		normalFlags(0);
		tag(0);
		colorIndex(-1);
		cullFace(null);
		material(IndigoRenderer.MATERIAL_STANDARD);
	}

	@Override
	public final MutableQuadViewImpl material(RenderMaterial material) {
		if (material == null) {
			material = IndigoRenderer.MATERIAL_STANDARD;
		}

		data[baseIndex + HEADER_BITS] = EncodingFormat.material(data[baseIndex + HEADER_BITS], (Value) material);
		return this;
	}

	@Override
	public final MutableQuadViewImpl cullFace(Direction face) {
		data[baseIndex + HEADER_BITS] = EncodingFormat.cullFace(data[baseIndex + HEADER_BITS], face);
		nominalFace(face);
		return this;
	}

	@Override
	public final MutableQuadViewImpl nominalFace(Direction face) {
		nominalFace = face;
		return this;
	}

	@Override
	public final MutableQuadViewImpl colorIndex(int colorIndex) {
		data[baseIndex + HEADER_COLOR_INDEX] = colorIndex;
		return this;
	}

	@Override
	public final MutableQuadViewImpl tag(int tag) {
		data[baseIndex + HEADER_TAG] = tag;
		return this;
	}

	/**
	 * @deprecated will be removed in 1.17 cycle - see docs in interface
	 */
	@Deprecated
	@Override
	public final MutableQuadViewImpl fromVanilla(int[] quadData, int startIndex, boolean isItem) {
		System.arraycopy(quadData, startIndex, data, baseIndex + HEADER_STRIDE, QUAD_STRIDE);
		isGeometryInvalid = true;
		return this;
	}

	@Override
	public final MutableQuadViewImpl fromVanilla(BakedQuad quad, RenderMaterial material, Direction cullFace) {
		System.arraycopy(quad.getVertexData(), 0, data, baseIndex + HEADER_STRIDE, QUAD_STRIDE);
		data[baseIndex + HEADER_BITS] = EncodingFormat.cullFace(0, cullFace);
		nominalFace(quad.getFace());
		colorIndex(quad.getColorIndex());
		material(material);
		tag(0);
		shade(quad.hasShade());
		isGeometryInvalid = true;
		return this;
	}

	@Override
	public MutableQuadViewImpl pos(int vertexIndex, float x, float y, float z) {
		final int index = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X;
		data[index] = Float.floatToRawIntBits(x);
		data[index + 1] = Float.floatToRawIntBits(y);
		data[index + 2] = Float.floatToRawIntBits(z);
		isGeometryInvalid = true;
		return this;
	}

	protected void normalFlags(int flags) {
		data[baseIndex + HEADER_BITS] = EncodingFormat.normalFlags(data[baseIndex + HEADER_BITS], flags);
	}

	@Override
	public MutableQuadViewImpl normal(int vertexIndex, float x, float y, float z) {
		normalFlags(normalFlags() | (1 << vertexIndex));
		data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL] = NormalHelper.packNormal(x, y, z, 0);
		return this;
	}

	/**
	 * Internal helper method. Copies face normals to vertex normals lacking one.
	 */
	public final void populateMissingNormals() {
		final int normalFlags = this.normalFlags();

		if (normalFlags == 0b1111) return;

		final int packedFaceNormal = NormalHelper.packNormal(faceNormal(), 0);

		for (int v = 0; v < 4; v++) {
			if ((normalFlags & (1 << v)) == 0) {
				data[baseIndex + v * VERTEX_STRIDE + VERTEX_NORMAL] = packedFaceNormal;
			}
		}

		normalFlags(0b1111);
	}

	@Override
	public MutableQuadViewImpl lightmap(int vertexIndex, int lightmap) {
		data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP] = lightmap;
		return this;
	}

	@Override
	public MutableQuadViewImpl spriteColor(int vertexIndex, int spriteIndex, int color) {
		Preconditions.checkArgument(spriteIndex == 0, "Unsupported sprite index: %s", spriteIndex);

		data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR] = color;
		return this;
	}

	@Override
	public MutableQuadViewImpl sprite(int vertexIndex, int spriteIndex, float u, float v) {
		Preconditions.checkArgument(spriteIndex == 0, "Unsupported sprite index: %s", spriteIndex);

		final int i = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U;
		data[i] = Float.floatToRawIntBits(u);
		data[i + 1] = Float.floatToRawIntBits(v);
		return this;
	}

	@Override
	public MutableQuadViewImpl spriteBake(int spriteIndex, Sprite sprite, int bakeFlags) {
		Preconditions.checkArgument(spriteIndex == 0, "Unsupported sprite index: %s", spriteIndex);

		TextureHelper.bakeSprite(this, spriteIndex, sprite, bakeFlags);
		return this;
	}
}
