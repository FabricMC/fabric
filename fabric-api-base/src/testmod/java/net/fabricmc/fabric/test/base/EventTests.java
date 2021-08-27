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

package net.fabricmc.fabric.test.base;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class EventTests {
	public static void run() {
		testDefaultPhaseOnly();
		testMultiplePhases();
		LogManager.getLogger("fabric-api-base").info("Event unit tests succeeded!");
	}

	private static int currentListener = 0;

	private static Event<Test> createEvent() {
		return EventFactory.createArrayBacked(Test.class, listeners -> () -> {
			for (Test test : listeners) {
				test.onTest();
			}
		});
	}

	private static Test ensureOrder(int order) {
		return () -> {
			assertEquals(order, currentListener);
			++currentListener;
		};
	}

	private static void testDefaultPhaseOnly() {
		Event<Test> event = createEvent();

		event.register(ensureOrder(0));
		event.register(Event.DEFAULT_PHASE, ensureOrder(1));
		event.register(ensureOrder(2));

		event.invoker().onTest();
		assertEquals(3, currentListener);
		currentListener = 0;
	}

	private static void testMultiplePhases() {
		Event<Test> event = createEvent();

		Identifier veryEarly = new Identifier("fabric:very_early");
		Identifier early = new Identifier("fabric:early");
		Identifier late = new Identifier("fabric:late");
		Identifier veryLate = new Identifier("fabric:very_late");

		event.addPhaseOrdering(veryEarly, early);
		event.addPhaseOrdering(early, Event.DEFAULT_PHASE);
		event.addPhaseOrdering(Event.DEFAULT_PHASE, late);
		event.addPhaseOrdering(late, veryLate);

		event.register(ensureOrder(4));
		event.register(ensureOrder(5));
		event.register(veryEarly, ensureOrder(0));
		event.register(early, ensureOrder(2));
		event.register(late, ensureOrder(6));
		event.register(veryLate, ensureOrder(8));
		event.register(veryEarly, ensureOrder(1));
		event.register(veryLate, ensureOrder(9));
		event.register(late, ensureOrder(7));
		event.register(early, ensureOrder(3));

		// Test a few dispatches.
		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(10, currentListener);
			currentListener = 0;
		}
	}

	@FunctionalInterface
	interface Test {
		void onTest();
	}

	private static void assertEquals(Object expected, Object actual) {
		if (!Objects.equals(expected, actual)) {
			throw new AssertionError(String.format("assertEquals failed%nexpected: %s%n but was: %s", expected, actual));
		}
	}
}
