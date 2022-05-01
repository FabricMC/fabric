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

package net.fabricmc.fabric.api.renderer.v1.mesh;

import net.minecraft.client.render.BufferBuilder;

/**
 * Similar in purpose to {@link BufferBuilder} but simpler
 * and not tied to NIO or any other specific implementation,
 * plus designed to handle both static and dynamic building.
 *
 * <p>Decouples models from the vertex format(s) used by
 * ModelRenderer to allow compatibility across diverse implementations.
 */
public interface MeshBuilder {
	/**
	 * Returns the {@link QuadEmitter} used to append quad to this mesh.
	 * Calling this method a second time invalidates any prior result.
	 * Do not retain references outside the context of building the mesh.
	 */
	QuadEmitter getEmitter();

	/**
	 * Returns a new {@link Mesh} instance containing all
	 * quads added to this builder and resets the builder to an empty state.
	 */
	Mesh build();
}
