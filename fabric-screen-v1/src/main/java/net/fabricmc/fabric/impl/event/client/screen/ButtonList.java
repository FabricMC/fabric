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

package net.fabricmc.fabric.impl.event.client.screen;

import java.util.AbstractList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

public class ButtonList<T extends AbstractButtonWidget> extends AbstractList<T> {

	private final List<T> buttons;
	private final List<Element> children;

	public ButtonList(List<T> buttons, List<Element> children) {
		this.buttons = buttons;
		this.children = children;
	}

	@Override
	public T get(int index) {
		return buttons.get(index);
	}

	@Override
	public T set(int index, T element) {
		checkIndex(index); // verify index bounds
		remove(element); // ensure no duplicates

		final T existingButton = buttons.get(index);

		int elementIndex = children.indexOf(existingButton);
		if (elementIndex > -1) {
			children.set(elementIndex, element);
		}

		return buttons.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		checkIndex(index); // verify index bounds
		remove(element); // ensure no duplicates

		buttons.add(index, element);
		children.add(Math.min(children.size(), index), element);
	}

	@Override
	public T remove(int index) {
		checkIndex(index); // verify index bounds

		final T removedButton = buttons.remove(index);
		index = children.indexOf(removedButton);

		if (index > -1) {
			children.remove(index);
		}

		return removedButton;
	}

	@Override
	public int size() {
		return buttons.size();
	}

	private void checkIndex(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("index < 0");
		}
		if (index > size()) {
			throw new IndexOutOfBoundsException("index > size()");
		}
	}
}
