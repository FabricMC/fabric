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

package net.fabricmc.fabric.impl.client.screen;

import java.util.AbstractList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// TODO: When events for listening to addition of child elements are added, fire events from this list.
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class ButtonList<T extends AbstractButtonWidget> extends AbstractList<T> {
	private final Screen screen;
	private final List<T> buttons;
	private final List<Element> children;

	public ButtonList(Screen screen, List<T> buttons, List<Element> children) {
		this.screen = screen;
		this.buttons = buttons;
		this.children = children;
	}

	@Override
	public T get(int index) {
		return this.buttons.get(index);
	}

	@Override
	public T set(int index, T element) {
		this.remove(element); // verify / ensure no duplicates

		final T existingButton = this.buttons.get(index);
		int elementIndex = this.children.indexOf(existingButton);

		if (elementIndex > -1) {
			this.children.set(elementIndex, element);
		}

		return this.buttons.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		this.rangeCheckForAdd(index); // verify index bounds
		this.remove(element); // ensure no duplicates

		this.buttons.add(index, element);
		this.children.add(Math.min(this.children.size(), index), element);
	}

	@Override
	public T remove(int index) {
		this.rangeCheck(index); // verify index bounds

		final T removedButton = this.buttons.remove(index);
		this.children.remove(removedButton);

		return removedButton;
	}

	@Override
	public int size() {
		return this.buttons.size();
	}

	private void rangeCheck(int index) {
		if (index >= this.size()) {
			throw createOutOfBoundsException(index);
		}
	}

	private void rangeCheckForAdd(int index) {
		if (index > this.size() || index < 0) {
			throw createOutOfBoundsException(index);
		}
	}

	private IndexOutOfBoundsException createOutOfBoundsException(int index) {
		return new IndexOutOfBoundsException("Index: " + index + ", Size: "+ this.size());
	}
}

