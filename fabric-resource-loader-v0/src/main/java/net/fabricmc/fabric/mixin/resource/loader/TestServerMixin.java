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

package net.fabricmc.fabric.mixin.resource.loader;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.resource.DataPackSettings;
import net.minecraft.test.TestServer;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

/**
 * Vanilla enables all available datapacks automatically in TestServer#create, but it does so in alphabetical order,
 * which means the Vanilla pack has higher precedence than modded, breaking our tests.
 */
@Mixin(TestServer.class)
public class TestServerMixin {
	@Redirect(method = "create", at = @At(value = "NEW", target = "(Ljava/util/List;Ljava/util/List;)Lnet/minecraft/resource/DataPackSettings;"))
	private static DataPackSettings replaceDefaultDataPackSettings(List<String> enabled, List<String> disabled) {
		return ModResourcePackUtil.createDefaultDataConfiguration().dataPacks();
	}
}
