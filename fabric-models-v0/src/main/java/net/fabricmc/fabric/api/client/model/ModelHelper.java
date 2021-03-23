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

import net.fabricmc.fabric.impl.client.model.BakedModelManagerHooks;

public final class ModelHelper {
	/**
	 * An alternative to {@link BakedModelManager#getModel(ModelIdentifier)} that accepts an {@link Identifier} instead.
	 * Models loaded using {@link ExtraModelProvider} do not have a corresponding ModelIdentifier, so that method cannot be used to retrieve them.
	 * The Identifier that was used to load them can be used in this method to retrieve them.
	 *
	 * @param manager The manager that holds models.
	 * @param id The id of the model.
	 * @return The model.
	 */
	@Nullable
	public static BakedModel getModel(BakedModelManager manager, Identifier id) {
		return ((BakedModelManagerHooks) manager).fabric_getModel(id);
	}

	private ModelHelper() { }
}
