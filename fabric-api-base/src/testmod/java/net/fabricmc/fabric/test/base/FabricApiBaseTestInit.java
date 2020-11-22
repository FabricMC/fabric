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

package net.fabricmc.fabric.test.base;

import static net.minecraft.server.command.CommandManager.literal;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.minecraft.text.LiteralText;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricApiBaseTestInit implements ModInitializer {
	private int ticks = 0;

	@Override
	public void onInitialize() {
		if (System.getProperty("fabric.autoTest") != null) {
			ServerTickEvents.END_SERVER_TICK.register(server -> {
				ticks++;

				if (ticks == 50) {
					MixinEnvironment.getCurrentEnvironment().audit();
					server.stop(false);
				}
			});
		}

		// Command to call audit the mixin environment
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("audit_mixins").executes(context -> {
				context.getSource().sendFeedback(new LiteralText("Auditing mixin environment"), false);

				try {
					MixinEnvironment.getCurrentEnvironment().audit();
				} catch (Exception e) {
					// Use an assertion error to bypass error checking in CommandManager
					throw new AssertionError("Failed to audit mixin environment", e);
				}

				context.getSource().sendFeedback(new LiteralText("Successfully audited mixin environment"), false);

				return 1;
			}));
		});
	}
}
