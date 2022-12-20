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

package net.fabricmc.fabric.api.gametest.v1;

import java.util.function.Function;

import net.minecraft.test.GameTestState;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;

/**
 * Extensions to {@link FabricTestContext}
 * for adding additional gametest functionality.
 *
 * <p>This interface is automatically injected to {@link TestContext}.
 */
public interface FabricTestContext {
	/**
	 * Creates an {@link EventSpy} instance that can be used to test {@link Event} invocations in a game test.
	 *
	 * <p><h3>Usage Example</h3>
	 * The following example shows how to spy on a test and ensure that it was called once.
	 * {@link EventSpy.Context#invoke(BlockPos)} must always be called to ensure that the event invocations are counted.
	 *
	 * <pre>{@code
	 * final EventSpy<TestEvent> spy = context.eventSpy(TEST_EVENT, spyCtx -> (blockPos -> {
	 *    if (spyCtx.invoke(blockPos)) {
	 *       // Handle event while test is running.
	 *    }
	 * }));
	 *
	 * // Regular test code goes here
	 * // Ensure that the event was called once
	 *  context.addFinalTask(spy::verifyCalledOnce);}</pre>
	 *
	 * @param event The {@link Event} instance to spy
	 * @param listenerFunction An {@link Function} implementation of that accepts a {@link EventSpy.Context} and returns an event listener.
	 * @return A {@link EventSpy} instance
	 */
	default <T> EventSpy<T> eventSpy(Event<T> event, Function<EventSpy.Context, T> listenerFunction) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default <T> EventSpy<T> eventSpy(Event<T> event, Identifier eventPhase, Function<EventSpy.Context, T> listenerFunction) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * @return The {@link GameTestState} for the current test
	 */
	default GameTestState getGameTestState() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
