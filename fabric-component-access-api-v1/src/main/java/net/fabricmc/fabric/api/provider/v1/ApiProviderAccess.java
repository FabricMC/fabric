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
 * Describes and provides access to component instances that may be retrieved
 * for blocks, items or entities.
 *
 * <p>This interface should never be implemented by mod authors. Create new instances
 * using {@link ApiProviderAccessRegistry#createAccess(net.minecraft.util.Identifier, Class, ApiProvider)}.
 *
 * @param <P> Identifies the API provider type
 * @param <A> Identifies the API type
 */
public interface ApiProviderAccess<P extends ApiProvider<P, A>, A> {
	/**
	 * Component value that will be returned when  an API is not present or not available.
	 *
	 * @return value to be returned when an API is not present or not available.
	 */
	A absentApi();

	/**
	 * An automatically constructed, immutable and non-allocating {@code ComponentAccess} instance
	 * that will always return the {@link #absentApi()} value.  Useful as default return value for access requests.
	 *
	 * @return an immutable, non-allocating {@code ComponentAccess} instance that will always return {@link #absentApi()}
	 */
	P absentProvider();

	/**
	 * The class associated with this component type. Exposed to support introspection.
	 *
	 * @return the class associated with this component type
	 */
	Class<A> apiType();

	/**
	 * Casts the input parameter to the component class associated with this component type.
	 *
	 * @param obj the object to be cast
	 * @return the input object cast to the component type
	 *
	 * @throws ClassCastException if the input object cannot be cast to the component class
	 */
	A castToApi(Object obj);
}
