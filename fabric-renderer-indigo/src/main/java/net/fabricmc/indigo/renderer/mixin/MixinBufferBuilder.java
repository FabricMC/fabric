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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.indigo.Indigo;
import net.fabricmc.indigo.renderer.accessor.AccessBufferBuilder;
import net.fabricmc.indigo.renderer.mesh.QuadViewImpl;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements AccessBufferBuilder {
    @Shadow private IntBuffer bufInt;
    @Shadow private int vertexCount;
    @Shadow abstract void grow(int size);
    @Shadow abstract int getCurrentSize();
    @Shadow public abstract VertexFormat getVertexFormat();

	private static final int VERTEX_STRIDE_INTS = 7;
    private static final int QUAD_STRIDE_INTS = VERTEX_STRIDE_INTS * 4;
    private static final int QUAD_STRIDE_BYTES = QUAD_STRIDE_INTS * 4;

    @Override
    public void fabric_putQuad(QuadViewImpl quad) {
        if(Indigo.ENSURE_VERTEX_FORMAT_COMPATIBILITY) {
            bufferCompatibly(quad);
        } else {
            bufferFast(quad);
        }
    }

    private void bufferFast(QuadViewImpl quad) {
        grow(QUAD_STRIDE_BYTES);
        bufInt.position(getCurrentSize());
        bufInt.put(quad.data(), quad.vertexStart(), QUAD_STRIDE_INTS);
        vertexCount += 4;
    }

    /**
     * Uses buffer vertex format to drive buffer population.
     * Relies on logic elsewhere to ensure coordinates don't include chunk offset
     * (because buffer builder will handle that.)<p>
     * 
     * Calling putVertexData() would likely be a little faster but this approach
     * gives us a chance to pass vertex normals to shaders, which isn't possible
     * with the standard block format. It also doesn't require us to encode a specific
     * custom format directly, which would be prone to breakage outside our control. 
     */
    private void bufferCompatibly(QuadViewImpl quad) {
        final VertexFormat format = getVertexFormat();;
        final int elementCount = format.getElementCount();
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < elementCount; j++) {
                VertexFormatElement e = format.getElement(j);
                switch(e.getType()) {
                case COLOR:
                    final int c = quad.spriteColor(i, 0);
                    ((BufferBuilder)(Object)this).color(c & 0xFF, (c >>> 8) & 0xFF, (c >>> 16) & 0xFF, (c >>> 24) & 0xFF);
                    break;
                case NORMAL:
                    ((BufferBuilder)(Object)this).normal(quad.normalX(i), quad.normalY(i), quad.normalZ(i));
                    break;
                case POSITION:
                    ((BufferBuilder)(Object)this).vertex(quad.x(i), quad.y(i), quad.z(i));
                    break;
                case UV:
                    if(e.getIndex() == 0) {
                        ((BufferBuilder)(Object)this).texture(quad.spriteU(i, 0), quad.spriteV(i, 0));
                    } else {
                        final int b = quad.lightmap(i);
                        ((BufferBuilder)(Object)this).texture((b >> 16) & 0xFFFF, b & 0xFFFF);
                    }
                    break;

                // these types should never occur and/or require no action
                case MATRIX:
                case BLEND_WEIGHT:
                case PADDING:
                default:
                    break;

                }
            }
            ((BufferBuilder)(Object)this).next();
        }
    }
}
