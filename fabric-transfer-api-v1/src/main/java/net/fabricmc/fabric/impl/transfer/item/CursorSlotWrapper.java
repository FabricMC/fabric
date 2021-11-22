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

package net.fabricmc.fabric.impl.transfer.item;

import java.util.Map;

import com.google.common.collect.MapMaker;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;

/**
 * Wrapper around the cursor slot of a screen handler.
 */
public class CursorSlotWrapper extends SingleStackStorage {
	private static final Map<ScreenHandler, CursorSlotWrapper> WRAPPERS = new MapMaker().weakValues().makeMap();

	public static CursorSlotWrapper get(ScreenHandler screenHandler) {
		return WRAPPERS.computeIfAbsent(screenHandler, CursorSlotWrapper::new);
	}

	private final ScreenHandler screenHandler;

	private CursorSlotWrapper(ScreenHandler screenHandler) {
		this.screenHandler = screenHandler;
	}

	@Override
	protected ItemStack getStack() {
		return screenHandler.getCursorStack();
	}

	@Override
	protected void setStack(ItemStack stack) {
		screenHandler.setCursorStack(stack);
	}
}
