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

package net.fabricmc.fabric.impl.networking.entity.v1;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.entity.v1.SpawnDataHandler;

public final class ComposedSpawnDataHandler<U extends Entity> implements SpawnDataHandler<U> {
	private final List<SpawnDataHandler<? super U>> entries;

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <U extends Entity> ComposedSpawnDataHandler<U> of(SpawnDataHandler<? super U>... entries) {
		List<SpawnDataHandler<? super U>> list = new ArrayList<>(entries.length);

		for (SpawnDataHandler<? super U> entry : entries) {
			if (entry instanceof ComposedSpawnDataHandler) {
				list.addAll(((ComposedSpawnDataHandler<? super U>) entry).entries);
			} else {
				list.add(entry);
			}
		}

		return new ComposedSpawnDataHandler(list);
	}

	@SuppressWarnings("unchecked")
	public static <U extends Entity> ComposedSpawnDataHandler<U> of(Iterable<SpawnDataHandler<? super U>> entries) {
		List<SpawnDataHandler<? super U>> list = new ArrayList<>();

		for (SpawnDataHandler<? super U> entry : entries) {
			if (entry instanceof ComposedSpawnDataHandler) {
				list.addAll(((ComposedSpawnDataHandler<? super U>) entry).entries);
			} else {
				list.add(entry);
			}
		}

		return new ComposedSpawnDataHandler<>(list);
	}

	private ComposedSpawnDataHandler(List<SpawnDataHandler<? super U>> entries) {
		this.entries = entries;
	}

	@Override
	public void write(U entity, PacketByteBuf buf) {
		for (SpawnDataHandler<? super U> entry : entries) {
			entry.write(entity, buf);
		}
	}

	@Override
	public void read(U entity, PacketByteBuf buf) {
		for (SpawnDataHandler<? super U> entry : entries) {
			entry.read(entity, buf);
		}
	}
}
