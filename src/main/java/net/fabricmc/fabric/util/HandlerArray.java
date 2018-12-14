/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.util;

import net.fabricmc.fabric.util.HandlerRegistry;

import java.lang.reflect.Array;

public class HandlerArray<T> implements HandlerRegistry<T> {
	private final Class tClass;
	private T[] array;

	@SuppressWarnings("unchecked")
	public HandlerArray(Class theClass) {
		this.tClass = theClass;
		this.array = (T[]) Array.newInstance(tClass, 0);
	}

	@Override
	public void register(T handler) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == handler) {
				throw new RuntimeException("Handler " + handler + " already registered!");
			}
		}

		//noinspection unchecked
		T[] newArray = (T[]) Array.newInstance(tClass, array.length + 1);
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length] = handler;
		array = newArray;
	}

	public T[] getBackingArray() {
		return array;
	}
}
