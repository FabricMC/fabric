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

import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.HEADER_BITS;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.HEADER_COLOR_INDEX;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.HEADER_MATERIAL;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.HEADER_STRIDE;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.HEADER_TAG;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.QUAD_STRIDE;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_COLOR;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_LIGHTMAP;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_NORMAL;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_STRIDE;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_U;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_V;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_X;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_Y;
import static net.fabricmc.indigo.renderer.mesh.EncodingFormat.VERTEX_Z;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.indigo.renderer.helper.NormalHelper;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

/**
 * Base class for all quads / quad makers. Handles the ugly bits
 * of maintaining and encoding the quad state.
 */
public class QuadViewImpl implements QuadView {
	protected RenderMaterialImpl.Value material;
	protected Direction cullFace;
	protected Direction nominalFace;
	protected Direction lightFace;
	protected int colorIndex = -1;
	protected int tag = 0;
	/** indicate if vertex normal has been set - bits correspond to vertex ordinals */
	protected int normalFlags;
	protected int geometryFlags;
	protected boolean isGeometryInvalid = true;
	protected final Vector3f faceNormal = new Vector3f();
	protected boolean isFaceNormalInvalid = true;

	/** Size and where it comes from will vary in subtypes. But in all cases quad is fully encoded to array. */
	protected int[] data;

	/** Beginning of the quad. Also the header index. */
	protected int baseIndex = 0;

	/**
	 * Use when subtype is "attached" to a pre-existing array.
	 * Sets data reference and index and decodes state from array.
	 */
	final void load(int[] data, int baseIndex) {
		this.data = data;
		this.baseIndex = baseIndex;
		load();
	}

	/**
	 * Used on vanilla quads or other quads that don't have encoded shape info
	 * to signal that such should be computed when requested.
	 */
	public final void invalidateShape() {
		isFaceNormalInvalid = true;
		isGeometryInvalid = true;
	}

	/**
	 * Like {@link #load(int[], int)} but assumes array and index already set.
	 * Only does the decoding part.
	 */
	public final void load() {
		// face normal isn't encoded but geometry flags are
		isFaceNormalInvalid = true;
		isGeometryInvalid = false;
		decodeHeader();
	}

	/** Reference to underlying array. Use with caution. Meant for fast renderer access */
	public int[] data() {
		return data;
	}

	/** True if any vertex normal has been set. */
	public boolean hasVertexNormals() {
		return normalFlags != 0;
	}

	/** reads state from header - vertex attributes are saved directly */
	protected void decodeHeader() {
		material = RenderMaterialImpl.byIndex(data[baseIndex + HEADER_MATERIAL]);
		final int bits = data[baseIndex + HEADER_BITS];
		colorIndex = data[baseIndex + HEADER_COLOR_INDEX];
		tag = data[baseIndex + HEADER_TAG];
		geometryFlags = EncodingFormat.geometryFlags(bits);
		cullFace = EncodingFormat.cullFace(bits);
		lightFace = EncodingFormat.lightFace(bits);
		nominalFace = lightFace;
		normalFlags = EncodingFormat.normalFlags(bits);
	}

	/** writes state to header - vertex attributes are saved directly */
	protected void encodeHeader() {
		data[baseIndex + HEADER_MATERIAL] = material.index();
		data[baseIndex + HEADER_COLOR_INDEX] = colorIndex;
		data[baseIndex + HEADER_TAG] = tag;
		int bits = EncodingFormat.geometryFlags(0, geometryFlags);
		bits = EncodingFormat.normalFlags(bits, normalFlags);
		bits = EncodingFormat.cullFace(bits, cullFace);
		bits = EncodingFormat.lightFace(bits, lightFace);
		data[baseIndex + HEADER_BITS] = bits;
	}

	/** gets flags used for lighting - lazily computed via {@link GeometryHelper#computeShapeFlags(QuadView)} */
	public int geometryFlags() {
		if (isGeometryInvalid) {
			isGeometryInvalid = false;
			geometryFlags = GeometryHelper.computeShapeFlags(this);
		}
		return geometryFlags;
	}

	/**
	 * Used to override geometric analysis for compatibility edge case 
	 */
	public void geometryFlags(int flags) {
		isGeometryInvalid = false;
		geometryFlags = flags;
	}

	@Override
	public final void toVanilla(int textureIndex, int[] target, int targetIndex, boolean isItem) {
		System.arraycopy(data, baseIndex + VERTEX_X, target, targetIndex, QUAD_STRIDE);
	}

	@Override
	public final RenderMaterialImpl.Value material() {
		return material;
	}

	@Override
	public final int colorIndex() {
		return colorIndex;
	}

	@Override
	public final int tag() {
		return tag;
	}

	@Override
	public final Direction lightFace() {
		return lightFace;
	}

	@Override
	public final Direction cullFace() {
		return cullFace;
	}

	@Override
	public final Direction nominalFace() {
		return nominalFace;
	}

	@Override
	public final Vector3f faceNormal() {
		if (isFaceNormalInvalid) {
			NormalHelper.computeFaceNormal(faceNormal, this);
			isFaceNormalInvalid = false;
		}
		return faceNormal;
	}

	@Override
	public void copyTo(MutableQuadView target) {
		MutableQuadViewImpl quad = (MutableQuadViewImpl) target;
		// copy everything except the header/material
		System.arraycopy(data, baseIndex + 1, quad.data, quad.baseIndex + 1, EncodingFormat.TOTAL_STRIDE - 1);
		quad.isFaceNormalInvalid = this.isFaceNormalInvalid;
		if (!this.isFaceNormalInvalid) {
			quad.faceNormal.set(this.faceNormal.getX(), this.faceNormal.getY(), this.faceNormal.getZ());
		}
		quad.lightFace = this.lightFace;
		quad.colorIndex = this.colorIndex;
		quad.tag = this.tag;
		quad.cullFace = this.cullFace;
		quad.nominalFace = this.nominalFace;
		quad.normalFlags = this.normalFlags;
	}

	@Override
	public Vector3f copyPos(int vertexIndex, Vector3f target) {
		if (target == null) {
			target = new Vector3f();
		}
		final int index = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X;
		target.set(Float.intBitsToFloat(data[index]), Float.intBitsToFloat(data[index + 1]), Float.intBitsToFloat(data[index + 2]));
		return target;
	}

	@Override
	public float posByIndex(int vertexIndex, int coordinateIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X + coordinateIndex]);
	}

	@Override
	public float x(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X]);
	}

	@Override
	public float y(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_Y]);
	}

	@Override
	public float z(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_Z]);
	}

	@Override
	public boolean hasNormal(int vertexIndex) {
		return (normalFlags & (1 << vertexIndex)) != 0;
	}

	protected final int normalIndex(int vertexIndex) {
		return baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL;
	}
	
	@Override
	public Vector3f copyNormal(int vertexIndex, Vector3f target) {
		if (hasNormal(vertexIndex)) {
			if (target == null) {
				target = new Vector3f();
			}
			final int normal = data[normalIndex(vertexIndex)];
			target.set(NormalHelper.getPackedNormalComponent(normal, 0), NormalHelper.getPackedNormalComponent(normal, 1), NormalHelper.getPackedNormalComponent(normal, 2));
			return target;
		} else {
			return null;
		}
	}

	@Override
	public float normalX(int vertexIndex) {
		return hasNormal(vertexIndex) ? NormalHelper.getPackedNormalComponent(data[normalIndex(vertexIndex)], 0) : Float.NaN;
	}

	@Override
	public float normalY(int vertexIndex) {
		return hasNormal(vertexIndex) ? NormalHelper.getPackedNormalComponent(data[normalIndex(vertexIndex)], 1) : Float.NaN;
	}

	@Override
	public float normalZ(int vertexIndex) {
		return hasNormal(vertexIndex) ? NormalHelper.getPackedNormalComponent(data[normalIndex(vertexIndex)], 2) : Float.NaN;
	}

	@Override
	public int lightmap(int vertexIndex) {
		return data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP];
	}

	@Override
	public int spriteColor(int vertexIndex, int textureIndex) {
		return data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR];
	}

	@Override
	public float spriteU(int vertexIndex, int textureIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U]);
	}

	@Override
	public float spriteV(int vertexIndex, int textureIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_V]);
	}

	public int vertexStart() {
		return baseIndex + HEADER_STRIDE;
	}
}
