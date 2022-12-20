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

package net.fabricmc.fabric.mixin.gametest;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.test.GameTestState;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.gametest.v1.EventSpy;
import net.fabricmc.fabric.api.gametest.v1.FabricTestContext;
import net.fabricmc.fabric.impl.gametest.EventSpyImpl;

@Mixin(TestContext.class)
public class TestContextMixin implements FabricTestContext {
	@Shadow
	@Final
	private GameTestState test;

	@Override
	public <T> EventSpy<T> eventSpy(Event<T> event, Function<EventSpy.Context, T> listenerFunction) {
		return new EventSpyImpl<>(event, listenerFunction, (TestContext) (Object) this);
	}

	@Override
	public <T> EventSpy<T> eventSpy(Event<T> event, Identifier eventPhase, Function<EventSpy.Context, T> listenerFunction) {
		return new EventSpyImpl<>(event, eventPhase, listenerFunction, (TestContext) (Object) this);
	}

	@Override
	public GameTestState getGameTestState() {
		return test;
	}
}
