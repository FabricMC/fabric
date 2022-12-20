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

import net.minecraft.test.GameTestException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EventSpy<T> {
	/**
	 * Verify that the event was invoked at least once.
	 *
	 * @throws GameTestException when the event has not been invoked.
	 */
	void verifyCalled();

	/**
	 * Verify that the event was invoked an exact number of times.
	 *
	 * @param expectedInvocations The expected number of event invocations.
	 * @throws GameTestException when the event has not been invoked the expected number of times.
	 */
	void verifyCalledTimes(int expectedInvocations);

	/**
	 * Verify that the event was never invoked.
	 *
	 * @throws GameTestException when the event has been invoked at least once.
	 */
	default void verifyNotCalled() {
		verifyCalledTimes(0);
	}

	/**
	 * Verify that the event was called once.
	 *
	 * @throws GameTestException when the event was not called once.
	 */
	default void verifyCalledOnce() {
		verifyCalledTimes(1);
	}

	/**
	 * @return the number of event invocations.
	 */
	int getInvocations();

	interface Context {
		/**
		 * Notify the {@link EventSpy} that the event was invoked.
		 *
		 * <p>The return value of this method should be used to determine whether the event should act upon the event.
		 * This method will return false after the test has completed or failed.
		 *
		 * <p>Any positional event should use {@link Context#invoke(BlockPos)} to ensure that the event is relevant to the current test.
		 *
		 * @return true when the test event handler should consume the event.
		 */
		boolean invoke();

		/**
		 * Notify the {@link EventSpy} that the event was invoked.
		 *
		 * <p>The return value of this method should be used to determine whether the event should act upon the event.
		 * This method will return false after the test has completed or failed.
		 * This method will return false when the {@link BlockPos} is not within the current test structure.
		 *
		 * @param blockPos the position in a {@link World}, used to determine if the event is related to the current test.
		 * @return true when the test event handler should consume the event.
		 */
		boolean invoke(BlockPos blockPos);
	}
}
