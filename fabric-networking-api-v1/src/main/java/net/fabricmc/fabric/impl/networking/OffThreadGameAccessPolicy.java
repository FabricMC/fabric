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

import java.util.Locale;

import net.fabricmc.fabric.api.networking.v1.ListenerContext;

public enum OffThreadGameAccessPolicy {
	PERMIT {
		@Override
		public void check(ListenerContext context, String content) {
		}
	},
	WARN {
		@Override
		public void check(ListenerContext context, String content) {
			if (!context.getEngine().isOnThread()) {
				NetworkingDetails.LOGGER.warn("Accessing {} out of the main thread!", content);
			}
		}
	},
	THROW {
		@Override
		public void check(ListenerContext context, String content) {
			if (!context.getEngine().isOnThread()) {
				throw new IllegalStateException(String.format("Accessing %s out of the main thread!", content));
			}
		}
	};

	public static OffThreadGameAccessPolicy parse(String propertyKey, OffThreadGameAccessPolicy fallback) {
		String value = System.getProperty(propertyKey);

		if (value == null) {
			return fallback;
		}

		try {
			return OffThreadGameAccessPolicy.valueOf(value.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			return fallback;
		}
	}

	public abstract void check(ListenerContext context, String content);
}
