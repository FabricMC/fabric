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

package net.fabricmc.fabric.impl.renderer;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;

public final class RendererAccessImpl implements RendererAccess {
	public static final RendererAccessImpl INSTANCE = new RendererAccessImpl();

	// private constructor
	private RendererAccessImpl() { }

	@Override
	public void registerRenderer(Renderer renderer) {
		if (renderer == null) {
			throw new NullPointerException("Attempt to register a NULL rendering plug-in.");
		} else if (activeRenderer != null) {
			throw new UnsupportedOperationException("A second rendering plug-in attempted to register. Multiple rendering plug-ins are not supported.");
		} else {
			activeRenderer = renderer;
			hasActiveRenderer = true;
		}
	}

	private Renderer activeRenderer = null;

	/** avoids null test every call to {@link #hasRenderer()}. */
	private boolean hasActiveRenderer = false;

	@Override
	public Renderer getRenderer() {
		return activeRenderer;
	}

	@Override
	public boolean hasRenderer() {
		return hasActiveRenderer;
	}
}
