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

package net.fabricmc.fabric.impl.client.crash.report.info;

import java.nio.file.Path;
import java.util.Locale;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.Bootstrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ReportType;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.mixin.client.crash.report.info.MinecraftClientAccessor;
import net.fabricmc.loader.api.FabricLoader;

public class ClientWatchdog implements ClientModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int DEFAULT_MAX_TIME_MS = 30000;
	private static final boolean ENABLED = FabricLoader.getInstance().isDevelopmentEnvironment() || Boolean.getBoolean("fabric.clientWatchdog.enabled");
	private static final int MAX_TIME_MS = Integer.getInteger("fabric.clientWatchdog.maxTimeMs", DEFAULT_MAX_TIME_MS);
	private volatile long tickStartTimeMs = -1;

	@Override
	public void onInitializeClient() {
		if (!ENABLED) return;
		ClientTickEvents.START_CLIENT_TICK.register((client) -> tickStartTimeMs = Util.getMeasuringTimeMs());
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
			Thread thread = new Thread(() -> run(client));
			thread.setName("Fabric Client Watchdog");
			thread.setDaemon(true);
			thread.start();
		});
	}

	public void run(MinecraftClient client) {
		while (client.isRunning()) {
			long tickStartTime = this.tickStartTimeMs;
			long currentTime = Util.getMeasuringTimeMs();
			long deltaMs = currentTime - tickStartTime;

			if (tickStartTime >= 0 && deltaMs > MAX_TIME_MS) {
				LOGGER.error(
						LogUtils.FATAL_MARKER,
						"A single client tick took {} seconds (should be max {})",
						String.format(Locale.ROOT, "%.2f", (float) deltaMs / 1000),
						String.format(Locale.ROOT, "%.2f", (float) MAX_TIME_MS / 1000)
				);
				LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, client will forcibly shutdown.");
				CrashReport report = DedicatedServerWatchdog.createCrashReport("Fabric Client Watchdog", ((MinecraftClientAccessor) client).getThread().threadId());
				client.addDetailsToCrashReport(report);
				Bootstrap.println("Crash report:\n" + report.asString(ReportType.MINECRAFT_CRASH_REPORT));
				Path path = client.runDirectory.toPath().resolve("crash-reports").resolve("crash-" + Util.getFormattedCurrentTime() + "-client.txt");

				if (report.writeToFile(path, ReportType.MINECRAFT_CRASH_REPORT)) {
					LOGGER.error("This crash report has been saved to: {}", path.toAbsolutePath());
				} else {
					LOGGER.error("We were unable to save this crash report to disk.");
				}

				System.exit(1);
			}
		}
	}
}
