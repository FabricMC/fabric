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
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.EventPhase;

public class EventTests {
	public static void run() {
		testDefaultPhaseOnly();
		testMultipleDefaultPhases();
		testAddedPhases();
		testCycle();
		LogManager.getLogger("fabric-api-base").info("Event unit tests succeeded!");
	}

	private static final Function<Test[], Test> INVOKER_FACTORY = listeners -> () -> {
		for (Test test : listeners) {
			test.onTest();
		}
	};

	private static int currentListener = 0;

	private static Event<Test> createEvent() {
		return EventFactory.createArrayBacked(Test.class, INVOKER_FACTORY);
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
		event.phase(EventPhase.DEFAULT).register(ensureOrder(1));
		event.register(ensureOrder(2));

		event.invoker().onTest();
		assertEquals(3, currentListener);
		currentListener = 0;
	}

	private static void testMultipleDefaultPhases() {
		Identifier first = new Identifier("fabric", "first");
		Identifier second = new Identifier("fabric", "second");
		Event<Test> event = EventFactory.createWithPhases(Test.class, INVOKER_FACTORY, first, second, EventPhase.DEFAULT);

		event.phase(second).register(ensureOrder(1));
		event.register(ensureOrder(2));
		event.phase(first).register(ensureOrder(0));

		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(3, currentListener);
			currentListener = 0;
		}
	}

	private static void testAddedPhases() {
		Event<Test> event = createEvent();

		Identifier veryEarly = new Identifier("fabric", "very_early");
		Identifier early = new Identifier("fabric", "early");
		Identifier late = new Identifier("fabric", "late");
		Identifier veryLate = new Identifier("fabric", "very_late");

		event.phase(veryEarly).runBefore(early);
		event.phase(early).runBefore(EventPhase.DEFAULT);
		event.phase(late).runAfter(EventPhase.DEFAULT);
		event.phase(late).runBefore(veryLate);

		event.register(ensureOrder(4));
		event.register(ensureOrder(5));
		event.phase(veryEarly).register(ensureOrder(0));
		event.phase(early).register(ensureOrder(2));
		event.phase(late).register(ensureOrder(6));
		event.phase(veryLate).register(ensureOrder(8));
		event.phase(veryEarly).register(ensureOrder(1));
		event.phase(veryLate).register(ensureOrder(9));
		event.phase(late).register(ensureOrder(7));
		event.phase(early).register(ensureOrder(3));

		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(10, currentListener);
			currentListener = 0;
		}
	}

	private static void testCycle() {
		Event<Test> event = createEvent();

		Identifier a = new Identifier("fabric", "a");
		Identifier b1 = new Identifier("fabric", "b1");
		Identifier b2 = new Identifier("fabric", "b2");
		Identifier b3 = new Identifier("fabric", "b3");
		Identifier c = EventPhase.DEFAULT;

		// A always first and C always last.
		event.phase(a).register(ensureOrder(0));
		event.phase(c).register(ensureOrder(4));
		event.phase(b1).register(ensureOrder(1));
		event.phase(b1).register(ensureOrder(2));
		event.phase(b1).register(ensureOrder(3));

		// A -> B
		event.phase(a).runBefore(b1);
		// B -> C
		event.phase(b3).runBefore(c);
		// loop
		event.phase(b1).runBefore(b2);
		event.phase(b2).runBefore(b3);
		event.phase(b3).runBefore(b1);

		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(5, currentListener);
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
