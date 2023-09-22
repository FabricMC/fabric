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

package net.fabricmc.fabric.test.base.client;

import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.closeScreen;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.openInventory;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.setPerspective;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.takeScreenshot;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.minecraft.client.option.Perspective;

public class FabricBaseInWorldTests implements FabricClientTest {
	@Override
	public Context getContext() {
		return Context.WORLD;
	}

	@ClientTest
	public void auditMixins() {
		MixinEnvironment.getCurrentEnvironment().audit();
	}

	@ClientTest
	public void playerRenderEvents() {
		// See if the player render events are working.
		setPerspective(Perspective.THIRD_PERSON_BACK);
		takeScreenshot("in_game_overworld_third_person");
		setPerspective(Perspective.FIRST_PERSON);
	}

	@ClientTest
	public void inGameInventory() {
		openInventory();
		takeScreenshot("in_game_inventory");
		closeScreen();
	}
}
