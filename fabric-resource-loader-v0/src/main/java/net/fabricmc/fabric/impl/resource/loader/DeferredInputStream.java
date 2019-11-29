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

package net.fabricmc.fabric.impl.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import net.minecraft.util.Unit;

/**
 * InputStream deferring to a separate I/O thread to work around
 * Thread.interrupt()-related issues in NIO.
 */
class DeferredInputStream extends InputStream {
	private final InputStream stream;

	public static InputStream deferIfNeeded(Callable<InputStream> streamSupplier) throws IOException {
		if (DeferredNioExecutionHandler.shouldDefer()) {
			return new DeferredInputStream(streamSupplier);
		} else {
			return DeferredNioExecutionHandler.submit(streamSupplier, false);
		}
	}

	DeferredInputStream(Callable<InputStream> streamSupplier) throws IOException {
		stream = DeferredNioExecutionHandler.submit(streamSupplier);

		if (stream == null) {
			throw new IOException("Something happened while trying to create an InputStream!");
		}
	}

	DeferredInputStream(InputStream stream) throws IOException {
		this.stream = stream;

		if (this.stream == null) {
			throw new IOException("Something happened while trying to create an InputStream!");
		}
	}

	@Override
	public int available() throws IOException {
		return DeferredNioExecutionHandler.submit(stream::available);
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public void mark(int readLimit) {
		stream.mark(readLimit);
	}

	@Override
	public void reset() throws IOException {
		DeferredNioExecutionHandler.submit(() -> {
			stream.reset();
			return Unit.INSTANCE;
		});
	}

	@Override
	public long skip(long n) throws IOException {
		return DeferredNioExecutionHandler.submit(() -> stream.skip(n));
	}

	@Override
	public int read() throws IOException {
		return DeferredNioExecutionHandler.submit(stream::read);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return DeferredNioExecutionHandler.submit(() -> stream.read(b));
	}

	@Override
	public int read(byte[] b, int offset, int length) throws IOException {
		return DeferredNioExecutionHandler.submit(() -> stream.read(b, offset, length));
	}

	@Override
	public void close() throws IOException {
		DeferredNioExecutionHandler.submit(() -> {
			stream.close();
			return Unit.INSTANCE;
		});
	}
}
