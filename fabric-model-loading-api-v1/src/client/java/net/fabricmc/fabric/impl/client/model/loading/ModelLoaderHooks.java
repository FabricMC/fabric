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

package net.fabricmc.fabric.impl.client.model.loading;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;

public interface ModelLoaderHooks {
	ModelLoadingEventDispatcher fabric_getDispatcher();

	UnbakedModel fabric_getMissingModel();

	UnbakedModel fabric_getOrLoadModel(Identifier id);

	void fabric_putModel(Identifier id, UnbakedModel model);

	void fabric_putModelDirectly(Identifier id, UnbakedModel model);

	void fabric_queueModelDependencies(UnbakedModel model);

	JsonUnbakedModel fabric_loadModelFromJson(Identifier id);
}
