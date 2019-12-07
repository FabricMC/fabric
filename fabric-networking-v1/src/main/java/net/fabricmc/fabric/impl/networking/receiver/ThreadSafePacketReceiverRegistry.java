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

package net.fabricmc.fabric.impl.networking.receiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.PacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiver;

class ThreadSafePacketReceiverRegistry<T extends PacketContext> extends SimplePacketReceiverRegistry<T> {
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public boolean register(Identifier channel, PacketReceiver<? super T> receiver) {
		Lock lock = this.lock.writeLock();

		try {
			lock.lock();
			return super.register(channel, receiver);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean unregister(Identifier channel) {
		Lock lock = this.lock.writeLock();

		try {
			lock.lock();
			return super.unregister(channel);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean receive(Identifier channel, T context, PacketByteBuf buf) {
		Lock lock = this.lock.readLock();

		try {
			lock.lock();
			return super.receive(channel, context, buf);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Collection<Identifier> getAcceptedChannels() {
		Lock lock = this.lock.readLock();

		try {
			lock.lock();
			return new ArrayList<>(super.getAcceptedChannels());
		} finally {
			lock.unlock();
		}
	}
}
