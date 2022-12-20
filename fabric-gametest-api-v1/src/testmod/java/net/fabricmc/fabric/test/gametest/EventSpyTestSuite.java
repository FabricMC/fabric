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

package net.fabricmc.fabric.test.gametest;

import java.util.function.Consumer;

import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.gametest.v1.EventSpy;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class EventSpyTestSuite implements FabricGameTest {
	@GameTest(templateName = EMPTY_STRUCTURE)
	public void eventSpyCalledOnce(TestContext context) {
		final EventSpy<TestEvent> spy = context.eventSpy(TEST_EVENT, spyCtx -> (spyCtx::invoke));

		TEST_EVENT.invoker().accept(context.getAbsolutePos(BlockPos.ORIGIN));

		context.addFinalTask(spy::verifyCalledOnce);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void eventSpyCalledExactly(TestContext context) {
		final EventSpy<TestEvent> spy = context.eventSpy(TEST_EVENT, spyCtx -> (spyCtx::invoke));

		for (int i = 0; i < 10; i++) {
			TEST_EVENT.invoker().accept(context.getAbsolutePos(BlockPos.ORIGIN));
		}

		context.addFinalTask(() -> spy.verifyCalledTimes(10));
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void eventSpyCalledMultiple(TestContext context) {
		final EventSpy<TestEvent> spy = context.eventSpy(TEST_EVENT, spyCtx -> (spyCtx::invoke));

		for (int i = 0; i < 10; i++) {
			TEST_EVENT.invoker().accept(context.getAbsolutePos(BlockPos.ORIGIN));
		}

		context.addFinalTask(spy::verifyCalled);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void eventSpyNotCalled(TestContext context) {
		final EventSpy<TestEvent> spy = context.eventSpy(TEST_EVENT, spyCtx -> (spyCtx::invoke));
		context.addFinalTask(spy::verifyNotCalled);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void eventSpyNotCalledOutOfBounds(TestContext context) {
		final EventSpy<TestEvent> spy = context.eventSpy(TEST_EVENT, spyCtx -> (spyCtx::invoke));

		TEST_EVENT.invoker().accept(new BlockPos(0, 512, 0));

		context.addFinalTask(spy::verifyNotCalled);
	}

	private static final Event<TestEvent> TEST_EVENT = EventFactory.createArrayBacked(TestEvent.class, callbacks -> (blockPos) -> {
		for (TestEvent callback : callbacks) {
			callback.accept(blockPos);
		}
	});

	@FunctionalInterface
	private interface TestEvent extends Consumer<BlockPos> {
	}
}
