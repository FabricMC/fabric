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

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;

/**
 * @deprecated Use {@link FabricBakedModelManager#getModel(Identifier)} instead.
 */
@Deprecated
public final class BakedModelManagerHelper {
	/**
	 * An alternative to {@link BakedModelManager#getModel(ModelIdentifier)} that accepts an
	 * {@link Identifier} instead. Models loaded using {@link ExtraModelProvider} do not have a
	 * corresponding {@link ModelIdentifier}, so the vanilla method cannot be used to retrieve them.
	 * The Identifier that was used to load them can be used in this method to retrieve them.
	 *
	 * <p><b>This method, as well as its vanilla counterpart, should only be used after the
	 * {@link BakedModelManager} has completed reloading.</b> Otherwise, the result will be
	 * null or an old model.
	 *
	 * @param manager the manager that holds models
	 * @param id the id of the model
	 * @return the model
	 */
	@Nullable
	public static BakedModel getModel(BakedModelManager manager, Identifier id) {
		return manager.getModel(id);
	}

	private BakedModelManagerHelper() { }
}
