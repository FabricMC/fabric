/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.client.model;

import net.minecraft.block.entity.BlockEntity;

/**
 * Interface for {@link BlockEntity}s which provide "render data".
 *
 * Render data is additional rendering information separate from the BlockState,
 * cached during chunk cache building on the main thread (safely) and accessible
 * during chunk rendering on non-main threads.
 *
 * For this reason, please ensure that all accesses to the passed render data are
 * thread-safe. This can be achieved by, for example, passing a pre-generated
 * immutable object, or ensuring all gets performed on the passed object are atomic
 * and well-checked for unusual states.
 */
public interface RenderDataProvidingBlockEntity {
	/**
	 * @return The render data provided by this object. Can be null.
	 */
	Object getRenderData();
}
