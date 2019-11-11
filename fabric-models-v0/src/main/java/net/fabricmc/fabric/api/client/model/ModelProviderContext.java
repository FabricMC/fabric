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

package net.fabricmc.fabric.api.client.model;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * The model loading context used during model providing.
 */
public interface ModelProviderContext {
	/**
	 * Load a model using a {@link Identifier}, {@link ModelIdentifier}, ...
	 *
	 * <p>Please note that the game engine keeps track of circular model loading calls on its own.
	 *
	 * @param id The model identifier.
	 * @return The UnbakedModel. Can return a missing model if it's not present!
	 */
	UnbakedModel loadModel(Identifier id);
}
