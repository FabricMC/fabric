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

package net.fabricmc.fabric.impl.gametest;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.gametest.v1.EventSpy;

public final class EventSpyImpl<T> implements EventSpy<T> {
	private final TestContext testContext;
	private final AtomicInteger invocationCount = new AtomicInteger();

	public EventSpyImpl(Event<T> event, Identifier eventPhase, Function<EventSpy.Context, T> listenerFunction, TestContext testContext) {
		this.testContext = testContext;
		event.register(eventPhase, listenerFunction.apply(new SpyContextImpl()));
	}

	public EventSpyImpl(Event<T> event, Function<EventSpy.Context, T> listenerFunction, TestContext testContext) {
		this.testContext = testContext;
		event.register(listenerFunction.apply(new SpyContextImpl()));
	}

	@Override
	public void verifyCalled() {
		final int callCount = getInvocations();

		if (callCount < 1) {
			throw new GameTestException("Expected event to be called, but was never called");
		}
	}

	public void verifyCalledTimes(int expectedInvocations) {
		final int actualInvocations = getInvocations();

		if (actualInvocations != expectedInvocations) {
			if (actualInvocations == 0) {
				throw new GameTestException(String.format(Locale.ROOT, "Expected event to be called %d times, but was never called", expectedInvocations));
			}

			throw new GameTestException(String.format(Locale.ROOT, "Expected event to be called %d times, but was called %s times", expectedInvocations, actualInvocations));
		}
	}

	@Override
	public int getInvocations() {
		return invocationCount.get();
	}

	private class SpyContextImpl implements EventSpy.Context {
		@Override
		public boolean invoke() {
			if (testContext.getGameTestState().isCompleted()) {
				// Test has finished
				return false;
			}

			invocationCount.getAndIncrement();

			return true;
		}

		@Override
		public boolean invoke(BlockPos blockPos) {
			final Box testBoundingBox = testContext.getGameTestState().getBoundingBox();

			if (testBoundingBox == null || !testBoundingBox.contains(Vec3d.of(blockPos))) {
				return false;
			}

			return invoke();
		}
	}
}
