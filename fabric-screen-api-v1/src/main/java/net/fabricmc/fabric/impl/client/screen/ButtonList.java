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

import net.minecraft.class_6379;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// TODO: When events for listening to addition of child elements are added, fire events from this list.
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class ButtonList extends AbstractList<ClickableWidget> {
	private final List<class_6379> listeners;
	private final List<Element> children;

	public ButtonList(List<class_6379> listeners, List<Element> children) {
		this.listeners = listeners;
		this.children = children;
	}

	@Override
	public ClickableWidget get(int index) {
		return (ClickableWidget) listeners.get(translateIndex(index, false));
	}

	@Override
	public ClickableWidget set(int index, ClickableWidget element) {
		index = translateIndex(index, false);
		int prevIndex = listeners.indexOf(element);

		if (prevIndex >= 0 && prevIndex != index) {
			if (prevIndex < index) index--;
			listeners.remove(prevIndex);
		}

		int childIndex = children.indexOf(element);

		if (childIndex >= 0) {
			children.set(childIndex, element);
		}

		return (ClickableWidget) listeners.set(index, element);
	}

	@Override
	public void add(int index, ClickableWidget element) {
		index = translateIndex(index, true);

		if (listeners.remove(element)) { // ensure no duplicates
			children.remove(element);
			index--;
		}

		listeners.add(index, element);
		this.children.add(element);
	}

	@Override
	public ClickableWidget remove(int index) {
		index = translateIndex(index, false);

		final ClickableWidget removedButton = (ClickableWidget) listeners.remove(index);
		this.children.remove(removedButton);

		return removedButton;
	}

	@Override
	public int size() {
		int ret = 0;

		for (class_6379 listener : listeners) {
			if (listener instanceof ClickableWidget) {
				ret++;
			}
		}

		return ret;
	}

	private int translateIndex(int index, boolean allowAfter) {
		int remaining = index;

		for (int i = 0, max = listeners.size(); i < max; i++) {
			if (listeners.get(i) instanceof ClickableWidget) {
				if (remaining == 0) return i;
				remaining--;
			}
		}

		if (allowAfter && remaining == 0) {
			return listeners.size();
		}

		throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", index, index - remaining));
	}
}

