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

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.command.DatapackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import net.fabricmc.fabric.impl.resource.loader.FabricResourcePackProfile;

/**
 * Disables enabling/disabling internal data packs.
 * Listing them is still allowed, but they do not appear in suggestions.
 */
@Mixin(DatapackCommand.class)
public class DatapackCommandMixin {
	@Unique
	private static final DynamicCommandExceptionType INTERNAL_PACK_EXCEPTION = new DynamicCommandExceptionType(
			packName -> Text.stringifiedTranslatable("commands.datapack.fabric.internal", packName));

	@Redirect(method = "method_13136", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;getEnabledIds()Ljava/util/Collection;"))
	private static Collection<String> filterEnabledPackSuggestions(ResourcePackManager dataPackManager) {
		return dataPackManager.getEnabledProfiles().stream().filter(profile -> !((FabricResourcePackProfile) profile).fabric_isHidden()).map(ResourcePackProfile::getId).toList();
	}

	@WrapOperation(method = "method_13120", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0))
	private static Stream<ResourcePackProfile> filterDisabledPackSuggestions(Stream<ResourcePackProfile> instance, Predicate<? super ResourcePackProfile> predicate, Operation<Stream<ResourcePackProfile>> original) {
		return original.call(instance, predicate).filter(profile -> !((FabricResourcePackProfile) profile).fabric_isHidden());
	}

	@Inject(method = "getPackContainer", at = @At(value = "INVOKE", target = "Ljava/util/Collection;contains(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE))
	private static void errorOnInternalPack(CommandContext<ServerCommandSource> context, String name, boolean enable, CallbackInfoReturnable<ResourcePackProfile> cir, @Local ResourcePackProfile profile) throws CommandSyntaxException {
		if (((FabricResourcePackProfile) profile).fabric_isHidden()) throw INTERNAL_PACK_EXCEPTION.create(profile.getId());
	}
}
