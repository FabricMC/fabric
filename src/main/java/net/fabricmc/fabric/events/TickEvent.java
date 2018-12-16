/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.events;

import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Profiler;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Events emitted during the ticking process for global Minecraft objects.
 * You can use them as endpoints to tick your own, related logic "globally".
 */
public final class TickEvent {
	public static final HandlerRegistry<Consumer<MinecraftServer>> SERVER = new HandlerArray<>(Consumer.class);
	public static final HandlerRegistry<Consumer<World>> WORLD = new HandlerArray<>(Consumer.class);

	private TickEvent() {

	}

	public static <T> void tick(HandlerRegistry<Consumer<T>> registry, T object, Profiler profiler) {
		Consumer<T>[] handlers = ((HandlerArray<Consumer<T>>) registry).getBackingArray();
		if (handlers.length > 0) {
			profiler.begin("fabric");

			int i = 0;
			for (Consumer<T> handler : handlers) {
				if ((i++) == 0) {
					profiler.begin(handler.getClass().getName());
				} else {
					profiler.endBegin(handler.getClass().getName());
				}
				handler.accept(object);
			}

			if (i > 0) {
				profiler.end();
			}
			profiler.end();
		}
	}
}
