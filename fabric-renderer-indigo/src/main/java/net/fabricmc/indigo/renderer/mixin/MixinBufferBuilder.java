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

package net.fabricmc.indigo.renderer.mixin;

import java.nio.IntBuffer;

import net.fabricmc.indigo.renderer.helper.BufferBuilderTransformHelper;
import net.fabricmc.indigo.renderer.mesh.EncodingFormat;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.indigo.renderer.accessor.AccessBufferBuilder;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements AccessBufferBuilder {
    @Shadow private IntBuffer bufInt;
    @Shadow private int vertexCount;
    @Shadow abstract void grow(int size);
    @Shadow abstract int getCurrentSize();
    @Shadow public abstract VertexFormat getVertexFormat();

	private static final int VERTEX_STRIDE_INTS = 7;
	private static final int VERTEX_STRIDE_BYTES = VERTEX_STRIDE_INTS * 4;
    private static final int QUAD_STRIDE_INTS = VERTEX_STRIDE_INTS * 4;
    private static final int QUAD_STRIDE_BYTES = QUAD_STRIDE_INTS * 4;

    private int fabric_processingMode;

    @Inject(at = @At("RETURN"), method = "begin")
	private void afterBegin(int mode, VertexFormat passedFormat, CallbackInfo info) {
    	fabric_processingMode = BufferBuilderTransformHelper.getProcessingMode(getVertexFormat());
	}

    /**
     * Similar to {@link BufferBuilder#putVertexData(int[])} but
     * accepts an array index so that arrays containing more than one
     * quad don't have to be copied to a transfer array before the call.
	 *
	 * It also always assumes the vanilla data format and is capable of
	 * transforming data from it to a different, non-vanilla data format.
     */
    @Override
    public void fabric_putVanillaData(int[] data, int start, boolean isItemFormat) {
    	switch (fabric_processingMode) {
			case BufferBuilderTransformHelper.MODE_COPY_FAST: {
				this.grow(QUAD_STRIDE_BYTES);
				this.bufInt.position(this.getCurrentSize());
				this.bufInt.put(data, start, QUAD_STRIDE_INTS);
			} break;
			case BufferBuilderTransformHelper.MODE_COPY_PADDED: {
				int currSize = this.getCurrentSize();
				int formatSizeBytes = getVertexFormat().getVertexSize();
				int formatSizeInts = formatSizeBytes / 4;
				this.grow(formatSizeBytes * 4);

				this.bufInt.position(currSize);
				this.bufInt.put(data, start, VERTEX_STRIDE_INTS);
				this.bufInt.position(currSize + formatSizeInts);
				this.bufInt.put(data, start + 7, VERTEX_STRIDE_INTS);
				this.bufInt.position(currSize + formatSizeInts * 2);
				this.bufInt.put(data, start + 14, VERTEX_STRIDE_INTS);
				this.bufInt.position(currSize + formatSizeInts * 3);
				this.bufInt.put(data, start + 21, VERTEX_STRIDE_INTS);
			} break;
    		case BufferBuilderTransformHelper.MODE_COPY_PADDED_SHADERSMOD: {
				int currSize = this.getCurrentSize();
				int formatSizeBytes = getVertexFormat().getVertexSize();
				int formatSizeInts = formatSizeBytes / 4;
				this.grow(formatSizeBytes * 4);

				this.bufInt.position(currSize);
				this.bufInt.put(data, start, VERTEX_STRIDE_INTS);
				this.bufInt.put(data[start + EncodingFormat.NORMALS_OFFSET_VANILLA]);
				this.bufInt.position(currSize + formatSizeInts);
				this.bufInt.put(data, start + 7, VERTEX_STRIDE_INTS);
				this.bufInt.put(data[start + EncodingFormat.NORMALS_OFFSET_VANILLA + 1]);
				this.bufInt.position(currSize + formatSizeInts * 2);
				this.bufInt.put(data, start + 14, VERTEX_STRIDE_INTS);
				this.bufInt.put(data[start + EncodingFormat.NORMALS_OFFSET_VANILLA + 2]);
				this.bufInt.position(currSize + formatSizeInts * 3);
				this.bufInt.put(data, start + 21, VERTEX_STRIDE_INTS);
				this.bufInt.put(data[start + EncodingFormat.NORMALS_OFFSET_VANILLA + 3]);
			} break;
    		case BufferBuilderTransformHelper.MODE_UNSUPPORTED:
    			// Don't emit any quads.
				BufferBuilderTransformHelper.emitUnsupportedError(getVertexFormat());
    			return;
		}

		this.vertexCount += 4;
    }
}
