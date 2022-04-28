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

package net.fabricmc.fabric.test.attachment.gametests;

import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class WorldTests {
	private static final AttachmentType<CustomData, World> CUSTOM_DATA = AttachmentType.forWorld(
			new Identifier("fabric-test", "custom_data"), CustomData.class, null
	);

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testWorld(TestContext context) {
		World world = context.getWorld();

		CUSTOM_DATA.remove(world);
		CUSTOM_DATA.computeIfAbsent(world, w -> new CustomData()).myInteger++;

		if (CUSTOM_DATA.get(world).myInteger != 1) {
			throw new AssertionError();
		}

		context.complete();
	}

	static class CustomData {
		int myInteger = 0;
	}
}
