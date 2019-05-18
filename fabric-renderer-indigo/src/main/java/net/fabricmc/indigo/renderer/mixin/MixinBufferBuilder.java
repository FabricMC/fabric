/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import net.fabricmc.indigo.renderer.accessor.AccessBufferBuilder;
import net.minecraft.client.render.BufferBuilder;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements AccessBufferBuilder {
    @Shadow private IntBuffer bufInt;
    @Shadow private int vertexCount;
    @Shadow private double offsetX;
    @Shadow private double offsetY;
    @Shadow private double offsetZ;
    @Shadow abstract void grow(int size);
    @Shadow abstract int getCurrentSize();
    
    private static final int QUAD_STRIDE_INTS = 28;
    private static final int QUAD_STRIDE_BYTES = QUAD_STRIDE_INTS * 4;
    
    /**
     * Similar to {@link BufferBuilder#putVertexData(int[])} but
     * accepts an array index so that arrays containing more than one
     * quad don't have to be copied to a transfer array before the call.
     */
    @Override
    public void fabric_putVanillaData(int[] data, int start) {
        this.grow(QUAD_STRIDE_BYTES);
        this.bufInt.position(this.getCurrentSize());
        this.bufInt.put(data, start, QUAD_STRIDE_INTS);
        this.vertexCount += 4;
    }
    @Override
    public double fabric_offsetX() {
        return offsetX;
    }
    @Override
    public double fabric_offsetY() {
        return offsetY;
    }
    @Override
    public double fabric_offsetZ() {
        return offsetZ;
    }
}
