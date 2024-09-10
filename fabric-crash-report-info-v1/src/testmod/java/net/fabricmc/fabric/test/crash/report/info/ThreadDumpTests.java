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

package net.fabricmc.fabric.test.crash.report.info;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ReportType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ThreadDumpTests implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadDumpTests.class);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("print_thread_dump_test_command").executes(this::executeDumpCommand)));
	}

	private int executeDumpCommand(CommandContext<ServerCommandSource> context) {
		final ServerCommandSource source = context.getSource();
		CrashReport crashReport = DedicatedServerWatchdog.createCrashReport("Watching Server", context.getSource().getServer().getThread().threadId());
		LOGGER.info(crashReport.asString(ReportType.MINECRAFT_CRASH_REPORT));
		source.sendFeedback(() -> Text.literal("Thread Dump printed to console."), false);
		return 1;
	}
}
