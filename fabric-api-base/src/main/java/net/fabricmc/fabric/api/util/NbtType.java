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

package net.fabricmc.fabric.api.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

/**
 * NBT type ID constants. Useful for filtering by value type in a few cases.
 *
 * @see NbtCompound#contains(String, int)
 * @see net.minecraft.nbt.NbtTypes#byId(int)
 *
 * @deprecated All NBT types are available in {@link NbtElement}. This
 * class would be removed in a future major version update.
 */
@Deprecated(forRemoval = true)
public final class NbtType {
	/**
	 * @see NbtElement#NULL_TYPE
	 */
	public static final int END = 0;
	/**
	 * @see NbtElement#BYTE_TYPE
	 */
	public static final int BYTE = 1;
	/**
	 * @see NbtElement#NULL_TYPE
	 */
	public static final int SHORT = 2;
	/**
	 * @see NbtElement#INT_TYPE
	 */
	public static final int INT = 3;
	/**
	 * @see NbtElement#LONG_TYPE
	 */
	public static final int LONG = 4;
	/**
	 * @see NbtElement#FLOAT_TYPE
	 */
	public static final int FLOAT = 5;
	/**
	 * @see NbtElement#DOUBLE_TYPE
	 */
	public static final int DOUBLE = 6;
	/**
	 * @see NbtElement#BYTE_ARRAY_TYPE
	 */
	public static final int BYTE_ARRAY = 7;
	/**
	 * @see NbtElement#STRING_TYPE
	 */
	public static final int STRING = 8;
	/**
	 * @see NbtElement#LIST_TYPE
	 */
	public static final int LIST = 9;
	/**
	 * @see NbtElement#COMPOUND_TYPE
	 */
	public static final int COMPOUND = 10;
	/**
	 * @see NbtElement#INT_ARRAY_TYPE
	 */
	public static final int INT_ARRAY = 11;
	/**
	 * @see NbtElement#LONG_ARRAY_TYPE
	 */
	public static final int LONG_ARRAY = 12;

	/**
	 * @see NbtElement#NUMBER_TYPE
	 */
	public static final int NUMBER = 99;

	private NbtType() { }
}
