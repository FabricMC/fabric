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

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;

import java.util.Collection;
import java.util.function.Function;

/**
 * Interface for model requesters.
 *
 * Model requesters are interfaces which request a collection of {@link ModelIdentifier} objects
 * pointing to specific models. In vanilla, it can be said that BlockStates and Items are
 * handled by such "model requesters", albeit without abstraction. A similar use is encouraged
 * here - if you have your own collection of objects which require in-world models, this is
 * the interface you should implement.
 */
public interface ModelRequester {
	/**
	 * @return All ModelIdentifiers requested by this ModelRequester.
	 */
	Collection<ModelIdentifier> getRequests();

	/**
     * Receive a way to access ModelIdentifiers asked for via getRequests().
	 *
	 * Please note that the behaviour of bakedModelGetter with ModelIdentifiers
	 * not asked for via getRequests is undefined and unsupported! Don't do that.
	 *
	 * @param bakedModelGetter The baked model getter for the given ModelIdentifiers.
	 */
	void receive(Function<ModelIdentifier, BakedModel> bakedModelGetter);
}
