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
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.NORMALS_OFFSET;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VANILLA_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_START_OFFSET;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.NormalHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.TextureHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper.ShadeableQuad;

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
		System.arraycopy(EMPTY, 0, data, baseIndex, EncodingFormat.MAX_STRIDE);
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
		final int vertexStart = vertexStart();

		if (isItem) {
			System.arraycopy(quadData, startIndex, data, vertexStart, 6);
			System.arraycopy(quadData, startIndex + 7, data, vertexStart + 7, 6);
			System.arraycopy(quadData, startIndex + 14, data, vertexStart + 14, 6);
			System.arraycopy(quadData, startIndex + 21, data, vertexStart + 21, 6);
			final int normalsIndex = baseIndex + NORMALS_OFFSET;
			data[normalsIndex] = quadData[startIndex + 6];
			data[normalsIndex + 1] = quadData[startIndex + 13];
			data[normalsIndex + 2] = quadData[startIndex + 20];
			data[normalsIndex + 3] = quadData[startIndex + 27];
		} else {
			System.arraycopy(quadData, startIndex, data, vertexStart, 28);
		}

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
		final int index = vertexStart() + vertexIndex * 7;
		data[index] = Float.floatToRawIntBits(x);
		data[index + 1] = Float.floatToRawIntBits(y);
		data[index + 2] = Float.floatToRawIntBits(z);
		isFaceNormalInvalid = true;
		return this;
	}

	@Override
	public MutableQuadViewImpl normal(int vertexIndex, float x, float y, float z) {
		normalFlags |= (1 << vertexIndex);
		data[baseIndex + VERTEX_START_OFFSET + VANILLA_STRIDE + vertexIndex] = NormalHelper.packNormal(x, y, z, 0);
		return this;
	}

	@Override
	public MutableQuadViewImpl lightmap(int vertexIndex, int lightmap) {
		data[baseIndex + vertexIndex * 7 + 6 + VERTEX_START_OFFSET] = lightmap;
		return this;
	}

	@Override
	public MutableQuadViewImpl spriteColor(int vertexIndex, int textureIndex, int color) {
		data[baseIndex + colorIndex(vertexIndex, textureIndex)] = color;
		return this;
	}

	@Override
	public MutableQuadViewImpl sprite(int vertexIndex, int textureIndex, float u, float v) {
		final int i = baseIndex + colorIndex(vertexIndex, textureIndex) + 1;
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
