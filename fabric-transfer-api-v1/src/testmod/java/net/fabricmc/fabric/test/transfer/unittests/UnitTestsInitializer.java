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

package net.fabricmc.fabric.test.transfer.unittests;

import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class UnitTestsInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		AttributeTests.run();
		BaseStorageTests.run();
		FluidItemTests.run();
		FluidTests.run();
		ItemTests.run();
		PlayerInventoryStorageTests.run();
		SingleVariantItemStorageTests.run();
		TransactionStateTests.run();
		UnderlyingViewTests.run();

		LoggerFactory.getLogger("fabric-transfer-api-v1 testmod").info("Transfer API unit tests successful.");
	}
}
