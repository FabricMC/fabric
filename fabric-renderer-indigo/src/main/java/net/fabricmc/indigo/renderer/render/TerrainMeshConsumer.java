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

package net.fabricmc.indigo.renderer.render;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.fabricmc.indigo.renderer.aocalc.AoCalculator;
import net.minecraft.client.util.math.Matrix4f;

public class TerrainMeshConsumer extends AbstractMeshConsumer {
	final Supplier<Matrix4f> matrixSupplier;
	
	TerrainMeshConsumer(TerrainBlockRenderInfo blockInfo, ChunkRenderInfo chunkInfo, AoCalculator aoCalc, QuadTransform transform, Supplier<Matrix4f> matrixSupplier) {
		super(blockInfo, chunkInfo::getInitializedBuffer, aoCalc, transform);
		this.matrixSupplier = matrixSupplier;
	}

	@Override
	protected Matrix4f matrix() {
		return matrixSupplier.get();
	}
}
