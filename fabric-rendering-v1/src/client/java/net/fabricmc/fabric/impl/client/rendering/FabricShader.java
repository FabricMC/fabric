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

package net.fabricmc.fabric.impl.client.rendering;

import java.io.IOException;

import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

public final class FabricShader extends Shader {
	public FabricShader(ResourceFactory factory, Identifier name, VertexFormat format) throws IOException {
		super(factory, name.toString(), format);
	}

	/**
	 * Rewrites the input string containing an identifier
	 * with the namespace of the id in the front instead of in the middle.
	 *
	 * <p>Example: {@code shaders/core/my_mod:xyz} -> {@code my_mod:shaders/core/xyz}
	 *
	 * @param input       the raw input string
	 * @param containedId the ID contained within the input string
	 * @return the corrected full ID string
	 */
	public static String rewriteAsId(String input, String containedId) {
		Identifier contained = new Identifier(containedId);
		return contained.getNamespace() + Identifier.NAMESPACE_SEPARATOR + input.replace(containedId, contained.getPath());
	}
}
