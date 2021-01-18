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

package net.fabricmc.fabric.api.client.event.input;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import net.fabricmc.fabric.api.client.FabricMouse;
import net.fabricmc.fabric.mixin.event.input.client.InputUtilTypeMixin;

public class MouseButtonEvent extends GenericMouseEvent {
	private static final Int2ObjectMap<Key> map = ((InputUtilTypeMixin) (Object) InputUtil.Type.MOUSE).getMap();

	public final int button;
	public final int action;
	public final int mods;

	public MouseButtonEvent(int button, int action, int mods) {
		super(FabricMouse.getX(), FabricMouse.getY(), 0.0, 0.0, FabricMouse.getPressedButtons(), FabricMouse.getMods());
		this.button = button;
		this.action = action;
		this.mods = mods;
	}

	public Key getKey() {
		return map.get(this.button);
	}
}
