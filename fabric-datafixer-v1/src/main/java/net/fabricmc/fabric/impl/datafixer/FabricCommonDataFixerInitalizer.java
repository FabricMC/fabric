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

package net.fabricmc.fabric.impl.datafixer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.impl.datafixer.test.TestObjects;
import net.fabricmc.fabric.impl.datafixer.test.TestUtil;

public final class FabricCommonDataFixerInitalizer implements ModInitializer {
	@Override
	public void onInitialize() {
		FabricDataFixerImpl.INSTANCE.isLocked(); // Load the DataFixers and register (Block)Entities and TypeReferences now.
		/** -- Testing -- **/

		TestObjects.create(); // Test Objects, remove before release.
		TestUtil.create(); // Remove before release, Tests are here for now.

		/** -- Testing -- **/

		// Once the server has started, we need to stop registering DataFixers. This is for world save safety purposes.
		ServerStartCallback.EVENT.register(server -> {
			if (!FabricDataFixerImpl.INSTANCE.isLocked()) {
				FabricDataFixerImpl.LOGGER.debug("Locked Registration from Server");
			}

			FabricDataFixerImpl.INSTANCE.lock();
		});
	}
}
