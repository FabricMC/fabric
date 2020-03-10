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

package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;

/**
 * Controls 1x warning for vanilla quad vertex format when running in compatibility mode.
 */
public abstract class CompatibilityHelper {
	private CompatibilityHelper() { }

	private static boolean logCompatibilityWarning = true;

	private static boolean isCompatible(int[] vertexData) {
		final boolean result = vertexData.length == EncodingFormat.QUAD_STRIDE;

		if (!result && logCompatibilityWarning) {
			logCompatibilityWarning = false;
			Indigo.LOGGER.warn("[Indigo] Encountered baked quad with non-standard vertex format. Some blocks will not be rendered");
		}

		return result;
	}

	public static boolean canRender(int[] vertexData) {
		return !Indigo.ENSURE_VERTEX_FORMAT_COMPATIBILITY || isCompatible(vertexData);
	}
}
