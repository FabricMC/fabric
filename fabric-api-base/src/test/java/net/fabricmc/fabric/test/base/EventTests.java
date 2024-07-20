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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.base.toposort.NodeSorting;

public class EventTests {
	private static final Function<TestCallback[], TestCallback> INVOKER_FACTORY = listeners -> () -> {
		for (TestCallback test : listeners) {
			test.onTest();
		}
	};

	private static int currentListener = 0;

	private static Event<TestCallback> createEvent() {
		return EventFactory.createArrayBacked(TestCallback.class, INVOKER_FACTORY);
	}

	private static TestCallback ensureOrder(int order) {
		return () -> {
			assertEquals(order, currentListener);
			++currentListener;
		};
	}

	@Test
	public void testDefaultPhaseOnly() {
		Event<TestCallback> event = createEvent();
		assertFalse(event.hasListener(), "Newly created event does not have listeners");

		event.register(ensureOrder(0));
		assertTrue(event.hasListener(), "hasListener returns true when event has a listener");
		event.register(Event.DEFAULT_PHASE, ensureOrder(1));
		event.register(ensureOrder(2));

		event.invoker().onTest();
		assertEquals(3, currentListener);
		currentListener = 0;
	}

	@Test
	public void testMultipleDefaultPhases() {
		Identifier first = Identifier.of("fabric", "first");
		Identifier second = Identifier.of("fabric", "second");
		Event<TestCallback> event = EventFactory.createWithPhases(TestCallback.class, INVOKER_FACTORY, first, second, Event.DEFAULT_PHASE);

		event.register(second, ensureOrder(1));
		assertTrue(event.hasListener(), "hasListener returns true when event has a listener in non-default phases");
		event.register(ensureOrder(2));
		event.register(first, ensureOrder(0));

		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(3, currentListener);
			currentListener = 0;
		}
	}

	@Test
	public void testAddedPhases() {
		Event<TestCallback> event = createEvent();

		Identifier veryEarly = Identifier.of("fabric", "very_early");
		Identifier early = Identifier.of("fabric", "early");
		Identifier late = Identifier.of("fabric", "late");
		Identifier veryLate = Identifier.of("fabric", "very_late");

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

		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(10, currentListener);
			currentListener = 0;
		}
	}

	@Test
	public void testCycle() {
		Event<TestCallback> event = createEvent();

		Identifier a = Identifier.of("fabric", "a");
		Identifier b1 = Identifier.of("fabric", "b1");
		Identifier b2 = Identifier.of("fabric", "b2");
		Identifier b3 = Identifier.of("fabric", "b3");
		Identifier c = Event.DEFAULT_PHASE;

		// A always first and C always last.
		event.register(a, ensureOrder(0));
		event.register(c, ensureOrder(4));
		event.register(b1, ensureOrder(1));
		event.register(b1, ensureOrder(2));
		event.register(b1, ensureOrder(3));

		// A -> B
		event.addPhaseOrdering(a, b1);
		// B -> C
		event.addPhaseOrdering(b3, c);
		// loop
		event.addPhaseOrdering(b1, b2);
		event.addPhaseOrdering(b2, b3);
		event.addPhaseOrdering(b3, b1);

		for (int i = 0; i < 5; ++i) {
			event.invoker().onTest();
			assertEquals(5, currentListener);
			currentListener = 0;
		}
	}

	/**
	 * Ensure that phases get sorted deterministically regardless of the order in which constraints are registered.
	 *
	 * <p>The graph is displayed here as ascii art, and also in the file graph.png.
	 * <pre>
	 *             +-------------------+
	 *             v                   |
	 * +---+     +---+     +---+     +---+
	 * | a | --> | z | --> | b | --> | y |
	 * +---+     +---+     +---+     +---+
	 *             ^
	 *             |
	 *             |
	 * +---+     +---+
	 * | d | --> | e |
	 * +---+     +---+
	 * +---+
	 * | f |
	 * +---+
	 * </pre>
	 * Notice the cycle z -> b -> y -> z. The elements of the cycle are ordered [b, y, z], and the cycle itself is ordered with its lowest id "b".
	 * We get for the final order: [a, d, e, cycle [b, y, z], f].
	 */
	@Test
	public void testDeterministicOrdering() {
		NodeSorting.ENABLE_CYCLE_WARNING = false;
		Identifier a = Identifier.of("fabric", "a");
		Identifier b = Identifier.of("fabric", "b");
		Identifier d = Identifier.of("fabric", "d");
		Identifier e = Identifier.of("fabric", "e");
		Identifier f = Identifier.of("fabric", "f");
		Identifier y = Identifier.of("fabric", "y");
		Identifier z = Identifier.of("fabric", "z");

		List<Consumer<Event<TestCallback>>> dependencies = List.of(
				ev -> ev.addPhaseOrdering(a, z),
				ev -> ev.addPhaseOrdering(d, e),
				ev -> ev.addPhaseOrdering(e, z),
				ev -> ev.addPhaseOrdering(z, b),
				ev -> ev.addPhaseOrdering(b, y),
				ev -> ev.addPhaseOrdering(y, z)
		);

		testAllPermutations(new ArrayList<>(), dependencies, selectedDependencies -> {
			Event<TestCallback> event = createEvent();

			for (Consumer<Event<TestCallback>> dependency : selectedDependencies) {
				dependency.accept(event);
			}

			event.register(a, ensureOrder(0));
			event.register(d, ensureOrder(1));
			event.register(e, ensureOrder(2));
			event.register(b, ensureOrder(3));
			event.register(y, ensureOrder(4));
			event.register(z, ensureOrder(5));
			event.register(f, ensureOrder(6));

			event.invoker().onTest();
			assertEquals(7, currentListener);
			currentListener = 0;
		});
		NodeSorting.ENABLE_CYCLE_WARNING = true;
	}

	/**
	 * TestCallback deterministic phase sorting with two cycles.
	 * <pre>
	 * e --> a <--> b <-- d <--> c
	 * </pre>
	 */
	@Test
	public void testTwoCycles() {
		NodeSorting.ENABLE_CYCLE_WARNING = false;
		Identifier a = Identifier.of("fabric", "a");
		Identifier b = Identifier.of("fabric", "b");
		Identifier c = Identifier.of("fabric", "c");
		Identifier d = Identifier.of("fabric", "d");
		Identifier e = Identifier.of("fabric", "e");

		List<Consumer<Event<TestCallback>>> dependencies = List.of(
				ev -> ev.addPhaseOrdering(e, a),
				ev -> ev.addPhaseOrdering(a, b),
				ev -> ev.addPhaseOrdering(b, a),
				ev -> ev.addPhaseOrdering(d, b),
				ev -> ev.addPhaseOrdering(d, c),
				ev -> ev.addPhaseOrdering(c, d)
		);

		testAllPermutations(new ArrayList<>(), dependencies, selectedDependencies -> {
			Event<TestCallback> event = createEvent();

			for (Consumer<Event<TestCallback>> dependency : selectedDependencies) {
				dependency.accept(event);
			}

			event.register(c, ensureOrder(0));
			event.register(d, ensureOrder(1));
			event.register(e, ensureOrder(2));
			event.register(a, ensureOrder(3));
			event.register(b, ensureOrder(4));

			event.invoker().onTest();
			assertEquals(5, currentListener);
			currentListener = 0;
		});
		NodeSorting.ENABLE_CYCLE_WARNING = true;
	}

	@SuppressWarnings("SuspiciousListRemoveInLoop")
	private static <T> void testAllPermutations(List<T> selected, List<T> toSelect, Consumer<List<T>> action) {
		if (toSelect.isEmpty()) {
			action.accept(selected);
		} else {
			for (int i = 0; i < toSelect.size(); ++i) {
				selected.add(toSelect.get(i));
				List<T> remaining = new ArrayList<>(toSelect);
				remaining.remove(i);
				testAllPermutations(selected, remaining, action);
				selected.removeLast();
			}
		}
	}

	@FunctionalInterface
	interface TestCallback {
		void onTest();
	}
}
