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

package net.fabricmc.fabric.impl.networking;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

public final class GlobalReceiverRegistry<H> {
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Identifier, H> handlers;

	public GlobalReceiverRegistry() {
		this(new HashMap<>()); // sync map should be fine as there is little read write competitions
	}

	public GlobalReceiverRegistry(Map<Identifier, H> map) {
		this.handlers = map;
	}

	@Nullable
	public H getHandler(Identifier channelName) {
		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return this.handlers.get(channelName);
		} finally {
			lock.unlock();
		}
	}

	public boolean registerGlobalReceiver(Identifier channelName, H handler) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");
		Objects.requireNonNull(handler, "Channel handler cannot be null");

		if (NetworkingImpl.isReservedPlayChannel(channelName)) {
			throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel with name \"%s\"", channelName));
		}

		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			return this.handlers.putIfAbsent(channelName, handler) == null;
		} finally {
			lock.unlock();
		}
	}

	public H unregisterGlobalReceiver(Identifier channelName) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		if (NetworkingImpl.isReservedPlayChannel(channelName)) {
			throw new IllegalArgumentException(String.format("Cannot unregister packet handler for reserved channel with name \"%s\"", channelName));
		}

		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			return this.handlers.remove(channelName);
		} finally {
			lock.unlock();
		}
	}

	public Map<Identifier, H> getHandlers() {
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			return new HashMap<>(this.handlers);
		} finally {
			lock.unlock();
		}
	}

	public Collection<Identifier> getChannels() {
		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return new HashSet<>(this.handlers.keySet());
		} finally {
			lock.unlock();
		}
	}

	public boolean hasChannel(Identifier channelName) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return this.handlers.containsKey(channelName);
		} finally {
			lock.unlock();
		}
	}
}
