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

package net.fabricmc.fabric.test.event.lifecycle.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class IntegratedServerLifecycleTests implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This should throw an illegal state exception, if it throws anything else, something is wrong. Should not return null since the supplier is setup in the api's ctor.
		try {
			ServerLifecycleEvents.getPrimaryServer();
		} catch (IllegalStateException ignored) {
			// Do nothing, this is intended
		}
	}
}
