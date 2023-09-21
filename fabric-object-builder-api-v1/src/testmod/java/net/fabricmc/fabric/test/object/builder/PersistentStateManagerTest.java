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

package net.fabricmc.fabric.test.object.builder;

import java.util.Objects;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class PersistentStateManagerTest implements ModInitializer {
	private boolean ranTests = false;

	@Override
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (ranTests) return;
			ranTests = true;

			TestState.getOrCreate(world).setValue("Hello!");
			assert Objects.equals(TestState.getOrCreate(world).getValue(), "Hello!");
		});
	}

	private static class TestState extends PersistentState {
		/**
		 * We are testing that null can be passed as the dataFixType.
		 */
		private static final PersistentState.Type<TestState> TYPE = new Type<>(TestState::new, TestState::fromTag, null);

		public static TestState getOrCreate(ServerWorld world) {
			return world.getPersistentStateManager().getOrCreate(TestState.TYPE, ObjectBuilderTestConstants.id("test_state").toString());
		}

		private String value = "";

		private TestState() {
		}

		private TestState(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
			markDirty();
		}

		@Override
		public NbtCompound writeNbt(NbtCompound nbt) {
			nbt.putString("value", value);
			return nbt;
		}

		private static TestState fromTag(NbtCompound tag) {
			return new TestState(tag.getString("value"));
		}
	}
}
