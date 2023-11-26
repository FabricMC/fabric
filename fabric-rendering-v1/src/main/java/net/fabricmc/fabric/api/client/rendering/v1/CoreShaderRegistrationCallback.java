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

package net.fabricmc.fabric.api.client.rendering.v1;

import java.io.IOException;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Called when core shaders ({@linkplain Shader shader programs} loaded from {@code assets/<namespace>/shaders/core})
 * are loaded to register custom modded shaders.
 *
 * <p>Fabric API also modifies the {@code #moj_import} feature in core shaders to accept
 * arbitrary namespaces for shaders loaded using the {@code <filename.glsl>} syntax.
 * For example, {@code #moj_import <my_mod:test.glsl>} would import the shader from
 * {@code assets/my_mod/shaders/include/test.glsl}.
 */
@FunctionalInterface
public interface CoreShaderRegistrationCallback {
	Event<CoreShaderRegistrationCallback> EVENT = EventFactory.createArrayBacked(CoreShaderRegistrationCallback.class, callbacks -> context -> {
		for (CoreShaderRegistrationCallback callback : callbacks) {
			callback.registerShaders(context);
		}
	});

	/**
	 * Registers core shaders using the registration context.
	 *
	 * @param context the registration context
	 */
	void registerShaders(RegistrationContext context) throws IOException;

	/**
	 * A context object used to create and register core shader programs.
	 *
	 * <p>This is not meant for implementation by users of the API.
	 */
	@ApiStatus.NonExtendable
	interface RegistrationContext {
		/**
		 * Creates and registers a core shader program.
		 *
		 * <p>The program is loaded from {@code assets/<namespace>/shaders/core/<path>.json}.
		 *
		 * @param id           the program ID
		 * @param vertexFormat the vertex format used by the shader
		 * @param loadCallback a callback that is called when the shader program has been successfully loaded
		 */
		void register(Identifier id, VertexFormat vertexFormat, Consumer<Shader> loadCallback) throws IOException;
	}
}
