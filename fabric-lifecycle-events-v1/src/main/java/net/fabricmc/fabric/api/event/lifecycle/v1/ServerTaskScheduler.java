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

package net.fabricmc.fabric.api.event.lifecycle.v1;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;

/**
 * Allows the scheduling of events after a specified number of server ticks.
 */
public class ServerTaskScheduler {
	static {
		// Don't add this the the event handler until the class is loaded
		ServerTickEvents.START_SERVER_TICK.register(ServerTaskScheduler::runExecutables);
	}

	private static final List<Pair<Integer, List<ServerTickEvents.StartTick>>> executables = new ArrayList<>();

	/**
	 * Adds a new event to be run after the specified {@code delayTicks}.
	 * @param executable the event to be run
	 * @param delayTicks the number of ticks to wait to run the event
	 */
	public static void execute(ServerTickEvents.StartTick executable, int delayTicks) {
		// See if there is already a pair with the same tick value
		for (Pair<Integer, List<ServerTickEvents.StartTick>> pair : executables) {
			if (pair.getLeft() == delayTicks) {
				pair.getRight().add(executable);
				return;
			}
		}

		// Add the new pair
		List<ServerTickEvents.StartTick> newList = new ArrayList<>();
		newList.add(executable);
		executables.add(new Pair<>(delayTicks, newList));
	}

	/**
	 * Adds a new event to be run the next tick.
	 * @param executable the event to be run
	 */
	public static void execute(ServerTickEvents.StartTick executable) {
		execute(executable, 1);
	}

	private static void runExecutables(MinecraftServer server) {
		for (Pair<Integer, List<ServerTickEvents.StartTick>> pair : executables) {
			int ticksLeft = pair.getLeft() - 1;

			if (ticksLeft <= 0) {
				for (ServerTickEvents.StartTick executable : pair.getRight()) {
					executable.onStartTick(server);
				}

				pair.getRight().removeIf(startTick -> true);
				executables.remove(pair);
				continue;
			}

			pair.setLeft(ticksLeft);
		}
	}

	private static class Pair<A, B> {
		private A left;
		private B right;

		Pair(A left, B right) {
			this.left = left;
			this.right = right;
		}

		public A getLeft() {
			return left;
		}

		public void setLeft(A a) {
			this.left = a;
		}

		public B getRight() {
			return right;
		}

		public void setRight(B right) {
			this.right = right;
		}
	}
}
