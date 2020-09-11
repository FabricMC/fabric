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

package net.fabricmc.fabric.api.provider.v1;

/**
 * Supplies API instances associated with a game object.
 *
 * <p>Consumers can obtain an instance of the provider
 * without knowing how or where it is implemented by using
 * {@link ApiProviderAccess#getAccess()}.
 *
 * <p>This decoupling of provider access from provider
 * implementation is the primary purpose of this module.
 *
 * <p>Implementations are expected to extend this interface to
 * add methods with additional parameters as needed.
 *
 * <p>Allocation and management of any state, along with thread
 * safety, are fully delegated to implementations.
 *
 * @param <P> Identifies the API provider type
 * @param <A> Identifies the API type
 */
@FunctionalInterface
public interface ApiProvider<P extends ApiProvider<P, A>, A> {
	/**
	 * Retrieves the API with the given access parameters, or {@link ApiProviderAccess#absentApi()} if the component
	 * is missing or inaccessible with the given parameters.
	 *
	 * <p>When this interface is overridden, this method should have
	 * a default implementation that returns what is appropriate when
	 * all provider parameters are missing, or {@link ApiProviderAccess#absentProvider()}
	 * if a null-input provider is not meaningful.
	 *
	 * @return An API instance or or {@link ApiProviderAccess#absentProvider()} if unavailable
	 */
	A getApi();
}
