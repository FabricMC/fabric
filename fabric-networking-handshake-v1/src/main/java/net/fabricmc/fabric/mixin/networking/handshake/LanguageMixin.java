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

package net.fabricmc.fabric.mixin.networking.handshake;

import java.util.Map;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.Language;
import net.minecraft.util.Util;

import net.fabricmc.fabric.impl.networking.handshake.LanguageInjection;

@Mixin(Language.class)
public abstract class LanguageMixin {
	@Shadow
	@Final
	private static Pattern field_11489;
	@Shadow
	@Final
	private Map<String, String> translations;

	@Redirect(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"))
	public long fabric_injectFabricLanguages() {
		LanguageInjection.injectHandshakeInfo(this.translations, field_11489);
		return Util.getMeasuringTimeMs();
	}
}
