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

package net.fabricmc.fabric.test.config;

import net.fabricmc.loader.api.config.data.Constraint;

public abstract class Bounds<T extends Number> extends Constraint<T> {
	protected final T min;
	protected final T max;

	protected Bounds(String name, T min, T max) {
		super(name);
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() {
		return "bounds[" + this.min + ", " + this.max + "]";
	}

	public static class Int extends Bounds<Integer> {
		public Int(Integer min, Integer max) {
			super("fabric:bounds/int", min, max);
		}

		@Override
		public boolean passes(Integer integer) {
			return integer >= this.min && integer <= this.max;
		}
	}
}
