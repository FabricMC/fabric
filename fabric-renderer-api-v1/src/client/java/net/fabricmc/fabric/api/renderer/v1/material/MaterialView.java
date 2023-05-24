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

package net.fabricmc.fabric.api.renderer.v1.material;

import net.fabricmc.fabric.api.util.TriState;

/**
 * Getter methods for {@link RenderMaterial} (immutable) and {@link MaterialFinder} (mutable).
 *
 * <p>Values returned by the getters may not necessarily be identical to those requested in the {@link MaterialFinder}.
 * The renderer may choose different values that are sufficiently representative for its own processing.
 */
public interface MaterialView {
	/**
	 * @see MaterialFinder#blendMode(BlendMode)
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default BlendMode blendMode() {
		return BlendMode.DEFAULT;
	}

	/**
	 * @see MaterialFinder#disableColorIndex(boolean)
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default boolean disableColorIndex() {
		return false;
	}

	/**
	 * @see MaterialFinder#emissive(boolean)
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default boolean emissive() {
		return false;
	}

	/**
	 * @see MaterialFinder#disableDiffuse(boolean)
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default boolean disableDiffuse() {
		return false;
	}

	/**
	 * @see MaterialFinder#ambientOcclusion(TriState)
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default TriState ambientOcclusion() {
		return TriState.DEFAULT;
	}

	/**
	 * @see MaterialFinder#glint(TriState)
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default TriState glint() {
		return TriState.DEFAULT;
	}
}
