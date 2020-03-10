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

package net.fabricmc.fabric.impl.client.indigo.renderer.accessor;

import net.minecraft.client.render.RenderLayer;

public interface AccessChunkRendererData {
	/**
	 * Mark internal tracking set that buffer has been initialized.
	 *
	 * @param renderLayer  Layer to be initialized.
	 * @return {@code true} if layer was not already initialized.
	 */
	boolean fabric_markInitialized(RenderLayer renderLayer);

	/**
	 * Mark internal tracking set that buffer has content.
	 *
	 * @param renderLayer  Layer with content.
	 */
	void fabric_markPopulated(RenderLayer renderLayer);
}
