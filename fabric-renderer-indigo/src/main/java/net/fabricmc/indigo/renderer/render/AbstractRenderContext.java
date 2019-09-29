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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.class_4587;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;

abstract class AbstractRenderContext implements RenderContext {
	private final ObjectArrayList<QuadTransform> transformStack = new ObjectArrayList<>();
	private static final QuadTransform NO_TRANSFORM = (q) -> true;
	protected class_4587 matrixStack;
	protected Matrix4f matrix;

	protected void prepareMatrix(BlockState blockState, BlockPos blockPos, BlockRenderView blockView, class_4587 matrixStack) {
		this.matrixStack = matrixStack;
		Vec3d vec = blockState.getOffsetPos(blockView, blockPos);
		matrixStack.method_22903();
		matrixStack.method_22904((double)(blockPos.getX() & 15) + vec.x, (double)(blockPos.getY() & 15) + vec.y, (double)(blockPos.getZ() & 15) + vec.z);
		matrix = matrixStack.method_22910();
	}

	private final QuadTransform stackTransform = (q) -> {
		int i = transformStack.size() - 1;

		while (i >= 0) {
			if (!transformStack.get(i--).transform(q)) {
				return false;
			}
		}

		return true;
	};

	private QuadTransform activeTransform = NO_TRANSFORM;

	protected final boolean transform(MutableQuadView q) {
		return activeTransform.transform(q);
	}

	protected boolean hasTransform() {
		return activeTransform != NO_TRANSFORM;
	}

	@Override
	public void pushTransform(QuadTransform transform) {
		if (transform == null) {
			throw new NullPointerException("Renderer received null QuadTransform.");
		}

		transformStack.push(transform);

		if (transformStack.size() == 1) {
			activeTransform = transform;
		} else if (transformStack.size() == 2) {
			activeTransform = stackTransform;
		}
	}

	@Override
	public void popTransform() {
		transformStack.pop();

		if (transformStack.size() == 0) {
			activeTransform = NO_TRANSFORM;
		} else if (transformStack.size() == 1) {
			activeTransform = transformStack.get(0);
		}
	}
}
