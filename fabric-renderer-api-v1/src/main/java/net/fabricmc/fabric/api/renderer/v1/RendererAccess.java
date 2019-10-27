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

package net.fabricmc.fabric.api.renderer.v1;

import net.fabricmc.fabric.impl.renderer.RendererAccessImpl;

/**
 * Registration and access for rendering extensions.
 */
public interface RendererAccess {
	RendererAccess INSTANCE = RendererAccessImpl.INSTANCE;

	/**
	 * Rendering extension mods must implement {@link Renderer} and
	 * call this method during initialization.
	 *
	 * <p>Only one {@link Renderer} plug-in can be active in any game instance.
	 * If a second mod attempts to register this method will throw an UnsupportedOperationException.
	 */
	void registerRenderer(Renderer plugin);

	/**
	 * Access to the current {@link Renderer} for creating and retrieving model builders
	 * and materials. Will return null if no render plug in is active.
	 */
	Renderer getRenderer();

	/**
	 * Performant test for {@link #getRenderer()} != null.
	 */
	boolean hasRenderer();
}
