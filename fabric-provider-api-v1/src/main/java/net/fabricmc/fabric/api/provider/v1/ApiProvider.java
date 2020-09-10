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
 * Supplies access to API instances associated with a game object.
 *
 * <p>This interface is for implementations.  Component consumers will
 * use {@link ComponentAccess} via {@link ApiProviderAccess#getAccess()}.
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
	 * @param side Side from which the component is being accessed
	 * @param id Identifier of a specific component or sub-component
	 * @return The component accessible via the given parameters
	 */
	A getApi();
}
