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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@Environment(EnvType.CLIENT)
public final class ClientLifecycleTests implements ClientModInitializer {
	private boolean startCalled;
	private boolean stopCalled;

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (startCalled) {
				throw new IllegalStateException("Start was already called!");
			}

			startCalled = true;
			client.submitAndJoin(() -> { // This should fail if the client thread was not bound yet.
				System.out.println("Started the client");
			});
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			if (stopCalled) {
				throw new IllegalStateException("Stop was already called!");
			}

			stopCalled = true;
			System.out.println("Client has started stopping!");
		});
	}
}
