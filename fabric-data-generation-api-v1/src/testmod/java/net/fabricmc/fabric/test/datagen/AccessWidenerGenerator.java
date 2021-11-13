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

package net.fabricmc.fabric.test.datagen;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.minecraft.data.server.RecipesProvider;

public class AccessWidenerGenerator {
	public static void main(String[] args) throws IOException {
		AccessWidenerGenerator generator = new AccessWidenerGenerator();

		generator.makeMethodsAccessible(RecipesProvider.class,
				BASE_PREDICATE.and(method -> !method.getName().equals("generate"))
		);

		Path template = Paths.get(Objects.requireNonNull(args[0], "No template path provided"));
		Path output = Paths.get(Objects.requireNonNull(args[1], "No output path provided"));

		String accessWidener = Files.readString(template, StandardCharsets.UTF_8) + generator;

		Files.writeString(output, accessWidener, StandardCharsets.UTF_8);
	}

	private final StringBuilder stringBuilder = new StringBuilder();

	private AccessWidenerGenerator() {
	}

	private void makeMethodsAccessible(Class clazz, Predicate<Method> methodPredicate) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (methodPredicate.test(method)) {
				stringBuilder.append("transitive-accessible\tmethod\t").append(clazz.getName().replace(".", "/"))
						.append('\t').append(method.getName())
						.append('\t').append(Type.getMethodDescriptor(method))
						.append('\n');
			}
		}
	}

	@Override
	public String toString() {
		return stringBuilder.toString();
	}

	private static final Predicate<Method> NOT_SYNTHETIC = method -> (method.getModifiers() & Opcodes.ACC_SYNTHETIC) == 0;
	private static final Predicate<Method> NOT_PUBLIC = method -> (method.getModifiers() & Opcodes.ACC_PUBLIC) == 0;
	private static final Predicate<Method> BASE_PREDICATE = NOT_SYNTHETIC.and(NOT_PUBLIC);
}
