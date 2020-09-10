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

package net.fabricmc.fabric.impl.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.ApiProviderAccess;

abstract class AbstractApiProviderAccess<P extends ApiProvider<P, A>, A> implements ApiProviderAccess<P, A> {
	final A absentApi;
	final P absentProvider;
	final Class<A> apiType;

	AbstractApiProviderAccess(Class<A> apiType, P absentProvider) {
		absentApi = absentProvider.getApi();
		this.absentProvider = absentProvider;
		this.apiType = apiType;
	}

	@Override
	public A absentApi() {
		return absentApi;
	}

	@Override
	public P absentProvider() {
		return absentProvider;
	}

	@Override
	public Class<A> apiType() {
		return apiType;
	}

	@Override
	public A castToApi(Object obj) {
		return apiType.cast(obj);
	}

	static final Logger LOGGER = LogManager.getLogger();
}
