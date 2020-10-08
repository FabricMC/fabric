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
 * Provides loosely-coupled access to APIs associated with game objects.
 *
 * @param <A> Identifies the API type
 */
public interface ApiProvider<A> {
	/**
	 * API instance to be returned when the API is not present or not available.
	 *
	 * @return instance to be returned when the API is not present or not available.
	 */
	A absentApi();

	/**
	 * The class for instances of the provided API. Exposed to support introspection.
	 *
	 * @return the class for instances of the provided API
	 */
	Class<A> apiType();

	/**
	 * Casts the input parameter to the class associated with the provided API.
	 *
	 * @param obj the object to be cast
	 * @return the input object cast to the API type
	 *
	 * @throws ClassCastException if the input object cannot be cast to the API class
	 */
	A castToApi(Object obj);
}
