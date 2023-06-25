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

package net.fabricmc.fabric.test.model.loading;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BakedModelRenderer {
	private static final Direction[] CULL_FACES = ArrayUtils.add(Direction.values(), null);
	private static final Random RANDOM = Random.create();

	public static void renderBakedModel(BakedModel model, VertexConsumer vertices, MatrixStack.Entry entry, int light) {
		for (Direction cullFace : CULL_FACES) {
			RANDOM.setSeed(42L);

			for (BakedQuad quad : model.getQuads(null, cullFace, RANDOM)) {
				vertices.quad(entry, quad, 1.0F, 1.0F, 1.0F, light, OverlayTexture.DEFAULT_UV);
			}
		}
	}
}
