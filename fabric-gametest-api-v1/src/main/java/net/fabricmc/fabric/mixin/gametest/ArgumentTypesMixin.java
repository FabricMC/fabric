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

package net.fabricmc.fabric.mixin.gametest;

import com.mojang.brigadier.arguments.ArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.SharedConstants;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.TestClassArgumentType;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.registry.Registry;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {
	@Shadow
	private static <A extends ArgumentType<?>, T extends ArgumentSerializer.class_7217<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String string, Class<? extends A> clazz, ArgumentSerializer<A, T> argumentSerializer) {
		throw new AssertionError("Nope.");
	}

	@Inject(method = "register(Lnet/minecraft/util/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", at = @At("RETURN"))
	private static void register(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> ci) {
		// Registered by vanilla when isDevelopment is enabled.
		if (!SharedConstants.isDevelopment) {
			register(registry, "test_argument", TestFunctionArgumentType.class, ConstantArgumentSerializer.method_41999(TestFunctionArgumentType::testFunction));
			register(registry, "test_class", TestClassArgumentType.class, ConstantArgumentSerializer.method_41999(TestClassArgumentType::testClass));
		}
	}
}
