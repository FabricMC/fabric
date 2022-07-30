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

package net.fabricmc.fabric.mixin.gamerule.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin(EditGameRulesScreen.class)
@Environment(EnvType.CLIENT)
public interface EditGameRulesScreenAccessor {
	@Invoker("markValid")
	void callMarkValid(EditGameRulesScreen.AbstractRuleWidget ruleWidget);

	@Invoker("markInvalid")
	void callMarkInvalid(EditGameRulesScreen.AbstractRuleWidget ruleWidget);
}
