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

package net.fabricmc.fabric.impl.recipe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ImmutableMapBuilderUtil {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final MethodHandle ENTRIES_GETTER;
	private static final MethodHandle SIZE_GETTER;

	private ImmutableMapBuilderUtil() {
		throw new UnsupportedOperationException("Someone tampered with the universe.");
	}

	static {
		try {
			Field field = ImmutableMap.Builder.class.getDeclaredField("entries");
			field.setAccessible(true);
			ENTRIES_GETTER = MethodHandles.lookup().unreflectGetter(field);
			field = ImmutableMap.Builder.class.getDeclaredField("size");
			field.setAccessible(true);
			SIZE_GETTER = MethodHandles.lookup().unreflectGetter(field);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			LOGGER.error("Could not access ImmutableMap$Builder entries or size fields. Which is necessary for the Recipe API.");
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Builds a mutable map from an immutable map.
	 * <p>This exists only because a builder will throw if a value is added 2 times. And copying a map is a bit bad.</p>
	 *
	 * @param builder the builder
	 * @param <K>     the key type
	 * @param <V>     the value type
	 * @return a mutable map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> specialBuild(ImmutableMap.Builder<K, V> builder) {
		try {
			Map.Entry<K, V>[] entries = (Map.Entry<K, V>[]) ENTRIES_GETTER.invoke(builder);
			int size = (int) SIZE_GETTER.invoke(builder);
			Map<K, V> map = new Object2ObjectOpenHashMap<>(size);

			for (Map.Entry<K, V> entry : entries) {
				if (entry == null) {
					continue;
				}

				map.put(entry.getKey(), entry.getValue());
			}

			return map;
		} catch (Throwable throwable) {
			LOGGER.error("Could not get values of ImmutableMap$Builder entries or size fields.");
			throw new IllegalStateException(throwable);
		}
	}
}
