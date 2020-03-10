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

package net.fabricmc.fabric.mixin.crash.report.info;

import java.util.Map;
import java.util.TreeMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

@Mixin(CrashReport.class)
public abstract class MixinCrashReport {
	@Shadow
	public abstract CrashReportSection getSystemDetailsSection();

	@Inject(at = @At("RETURN"), method = "fillSystemDetails")
	private void fillSystemDetails(CallbackInfo info) {
		getSystemDetailsSection().add("Fabric Mods", () -> {
			Map<String, String> mods = new TreeMap<>();

			for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
				mods.put(container.getMetadata().getId(), container.getMetadata().getName() + " " + container.getMetadata().getVersion().getFriendlyString());
			}

			StringBuilder modString = new StringBuilder();

			for (String id : mods.keySet()) {
				modString.append("\n\t\t");
				modString.append(id);
				modString.append(": ");
				modString.append(mods.get(id));
			}

			return modString.toString();
		});
	}
}
