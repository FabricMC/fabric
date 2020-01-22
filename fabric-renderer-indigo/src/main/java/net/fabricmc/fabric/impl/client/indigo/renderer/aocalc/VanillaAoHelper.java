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

package net.fabricmc.fabric.impl.client.indigo.renderer.aocalc;

import java.lang.reflect.Constructor;
import java.util.BitSet;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessAmbientOcclusionCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessBlockModelRenderer;
import net.fabricmc.loader.api.FabricLoader;

public class VanillaAoHelper {
	private static Supplier<AccessAmbientOcclusionCalculator> factory;

	// Renderer method we call isn't declared as static, but uses no
	// instance data and is called from multiple threads in vanilla also.
	private static AccessBlockModelRenderer blockRenderer;

	public static void initialize(BlockModelRenderer instance) {
		blockRenderer = (AccessBlockModelRenderer) instance;

		final String target = FabricLoader.getInstance().getMappingResolver()
				.mapClassName("intermediary", "net.minecraft.class_778$class_780");

		for (Class<?> innerClass : BlockModelRenderer.class.getDeclaredClasses()) {
			if (innerClass.getName().equals(target)) {
				Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
				constructor.setAccessible(true);

				factory = new Supplier<AccessAmbientOcclusionCalculator>() {
					@Override
					public AccessAmbientOcclusionCalculator get() {
						try {
							return (AccessAmbientOcclusionCalculator) constructor.newInstance(instance);
						} catch (Exception e) {
							Indigo.LOGGER.warn("[Indigo] Exception accessing vanilla smooth lighter", e);
							return null;
						}
					}
				};
				break;
			}
		}

		if (factory != null && factory.get() == null) {
			factory = null;
		}

		if (factory == null) {
			Indigo.LOGGER.warn("[Indigo] Vanilla smooth lighter unavailable. Indigo lighter will be used even if not configured.");
		}
	}

	public static AccessAmbientOcclusionCalculator get() {
		return factory == null ? null : factory.get();
	}

	public static void updateShape(BlockRenderView blockRenderView, BlockState blockState, BlockPos pos, int[] vertexData, Direction face, float[] aoData, BitSet controlBits) {
		blockRenderer.fabric_updateShape(blockRenderView, blockState, pos, vertexData, face, aoData, controlBits);
	}
}
