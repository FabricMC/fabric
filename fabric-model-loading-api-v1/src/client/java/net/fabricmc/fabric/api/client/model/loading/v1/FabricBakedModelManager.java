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

package net.fabricmc.fabric.api.client.model.loading.v1;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Fabric-provided helper methods for {@link BakedModelManager}.
 *
 * <p>Note: This interface is automatically implemented on the {@link BakedModelManager} via Mixin and interface injection.
 */
public interface FabricBakedModelManager {
	/**
	 * Similar to {@link BakedModelManager#getModel(ModelIdentifier)}, but accepts an {@link Identifier} instead of a
	 * {@link ModelIdentifier}. Use this method to retrieve models loaded using
	 * {@link ModelLoadingPlugin.Context#addModels}, since those models do not have corresponding
	 * {@link ModelIdentifier}s.
	 *
	 * <p><b>This method, as well as its vanilla counterpart, should only be used after the
	 * {@link BakedModelManager} has completed reloading.</b> Otherwise, the result will be
	 * outdated or an exception will be thrown.
	 *
	 * @param id the id of the model
	 * @return the model
	 */
	default BakedModel getModel(Identifier id) {
		throw new UnsupportedOperationException("Implemented via mixin.");
	}
}
