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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

class DeferredNioExecutionHandler {
	// private static final ThreadLocal<Boolean> DEFERRED_REQUIRED = new ThreadLocal<>();
	private static final boolean DEFER_REQUESTED = System.getProperty("fabric.resource-loader.deferFilesystemOperations", "false").equalsIgnoreCase("true");
	private static ExecutorService EXECUTOR_SERVICE;

	public static boolean shouldDefer() {
		return DEFER_REQUESTED;
		/* Boolean deferRequired = DEFERRED_REQUIRED.get();

		if (deferRequired == null) {
			deferRequired = false;

			StackTraceElement[] elements = Thread.currentThread().getStackTrace();

			for (int i = 0; i < elements.length; i++) {
				if (elements[i].getClassName().startsWith("paulscode.sound.")) {
					deferRequired = true;
					break;
				}
			}

			DEFERRED_REQUIRED.set(deferRequired);
		}

		return deferRequired; */
	}

	public static <V> V submit(Callable<V> callable, boolean cond) throws IOException {
		try {
			return cond ? submit(callable) : callable.call();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Exception which should not happen!", e);
		}
	}

	public static <V> V submit(Callable<V> callable) throws IOException {
		if (EXECUTOR_SERVICE == null) {
			EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(
				new ThreadFactoryBuilder()
					.setNameFormat("Fabric Deferred I/O Thread")
					.build()
			);
		}

		Future<V> future = EXECUTOR_SERVICE.submit(callable);
		return getSubmittedFuture(future);
	}

	static <V> V getSubmittedFuture(Future<V> future) throws IOException {
		while (true) {
			try {
				return future.get();
			} catch (ExecutionException e) {
				Throwable t = e.getCause();

				if (t instanceof IOException) {
					throw (IOException) t;
				} else {
					throw new RuntimeException("ExecutionException which should not happen!", t);
				}
			} catch (InterruptedException e) {
				// keep calm, carry on...
			}
		}
	}
}

