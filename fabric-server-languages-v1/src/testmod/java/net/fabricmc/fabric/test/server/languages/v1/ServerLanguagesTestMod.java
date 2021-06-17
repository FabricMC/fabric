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

package net.fabricmc.fabric.test.server.languages.v1;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLanguagesTestMod implements DedicatedServerModInitializer {
	private static final String MOD_ID = "fabric-server-languages-v1-testmod";
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	private static boolean allTestsPassed = true;

	private static void logTestResult(boolean passed, String testMessage, String testValue) {
		if (passed) {
			LOGGER.info("Passed: {} -> [{}]", testMessage, testValue);
		} else {
			LOGGER.error("FAILED: {} -> [{}]", testMessage, testValue);
			allTestsPassed = false;
		}
	}

	@Override
	public void onInitializeServer() {
		// One mod provided translation key and one minecraft key.
		TranslatableText hello = new TranslatableText("server.languages.hello");
		TranslatableText potato = new TranslatableText("item.minecraft.potato");


		// Make sure we're still loading minecraft's translation keys.
		logTestResult(
				potato.getString().equals("Potato"),
				"Minecraft translation keys are still loaded",
				potato.getString()
		);

		// Mod translation keys should be loaded by default, unlike vanilla.
		logTestResult(
				hello.getString().equals("Hello!"),
				"Mod provided translation keys are loaded by default",
				hello.getString()
		);

		logTestResult(
				allTestsPassed,
				"All tests passed",
				String.valueOf(allTestsPassed)
		);

		assert allTestsPassed;
	}
}
