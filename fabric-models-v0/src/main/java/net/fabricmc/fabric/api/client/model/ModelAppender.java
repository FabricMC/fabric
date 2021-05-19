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

/**
 * @deprecated use {@link ExtraModelProvider}, which supports loading of plain {@link Identifier}s.
 * This class would be removed in a future major version update.
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface ModelAppender {
	@Deprecated(forRemoval = true)
	void appendAll(ResourceManager manager, Consumer<ModelIdentifier> out);
}
