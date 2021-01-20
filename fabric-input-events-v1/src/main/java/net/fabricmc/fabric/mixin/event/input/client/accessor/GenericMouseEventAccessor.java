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

package net.fabricmc.fabric.mixin.event.input.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.fabric.api.event.client.input.GenericMouseEvent;

@Mixin(GenericMouseEvent.class)
public interface GenericMouseEventAccessor {
	@Accessor
	void setCursorX(double dx);

	@Accessor
	void setCursorY(double dx);

	@Accessor
	void setCursorDeltaX(double dy);

	@Accessor
	void setCursorDeltaY(double dy);

	@Accessor
	void setScrollX(double dx);

	@Accessor
	void setScrollY(double dy);

	@Accessor
	void setPressedButtons(int pressedButtons);

	@Accessor
	void setPressedModKeys(int modKeys);
}
