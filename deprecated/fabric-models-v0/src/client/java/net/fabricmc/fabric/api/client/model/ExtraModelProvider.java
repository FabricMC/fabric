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

import java.util.function.Consumer;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

/**
 * @deprecated Use {@link ModelLoadingPlugin} and related classes instead.
 */
@Deprecated
@FunctionalInterface
public interface ExtraModelProvider {
	/**
	 * Provides an opportunity inform the game that you would like it to load and bake a model,
	 * even if that model is not used by any blocks or items.
	 * @param out Accepts paths to be loaded. Arguments that are {@link ModelIdentifier} will be
	 *            loaded through the blockstate JSON system or, if the variant is {@code inventory}, the item model folder.
	 *            Otherwise, the argument is directly loaded as a JSON.
	 *            For example, <pre>new Identifier("mymod", "foo/bar")</pre> will request loading of the file
	 *            <pre>/assets/mymod/models/foo/bar.json</pre>
	 */
	void provideExtraModels(ResourceManager manager, Consumer<Identifier> out);
}
