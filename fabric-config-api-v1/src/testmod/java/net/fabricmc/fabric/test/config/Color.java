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

public class Color {
	public final int value;
	public final int a;
	public final int r;
	public final int g;
	public final int b;

	public Color(int value) {
		this.value = value;
		this.a = (value >> 24) & 0xFF;
		this.r = (value >> 16) & 0xFF;
		this.g = (value >> 8) & 0xFF;
		this.b = value & 0xFF;
	}
}
