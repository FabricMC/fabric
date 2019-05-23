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

package net.fabricmc.indigo.renderer.helper;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class BufferBuilderTransformHelper {
	/**
	 * Fast copying mode; used only if the vanilla format is an exact match.
	 */
	public static final int MODE_COPY_FAST = 0;

	/**
	 * Padded copying mode; used when the vanilla format is an exact match,
	 * but includes additional data at the end. Will emit a warning.
	 */
	public static final int MODE_COPY_PADDED = 1;

	/**
	 * ShadersMod compatibility mode; as MODE_COPY_PADDED, but populates in
	 * the correct normal values as provided by the mod.
	 *
	 * Assumes a format of [vertex, color, texture, lmap, normal], all in
	 * their respective vanilla formats, plus any amount of additional data
	 * afterwards.
	 */
	public static final int MODE_COPY_PADDED_SHADERSMOD = 2;

	/**
	 * Unsupported mode; an error will be emitted and no quads will be
	 * pushed to the buffer builder.
	 */
	public static final int MODE_UNSUPPORTED = 3;

	private static final Object2IntMap<VertexFormat> vertexFormatCache = new Object2IntOpenHashMap<>();
	private static final Set<VertexFormat> errorEmittedFormats = Sets.newIdentityHashSet();
	private static final Logger logger = LogManager.getLogger();

	public static void emitUnsupportedError(VertexFormat format) {
		if (errorEmittedFormats.add(format)) {
			logger.error("[Indigo] Unsupported vertex format! " + format);
		}
	}

	public static int getProcessingMode(VertexFormat format) {
		return vertexFormatCache.computeIntIfAbsent(format, (f) -> {
			// Check for vanilla-compatible prefix

			if (
				f.getElementCount() >= 4 && f.getVertexSizeInteger() >= 7
				&& f.getElement(0).equals(VertexFormats.POSITION_ELEMENT)
				&& f.getElement(1).equals(VertexFormats.COLOR_ELEMENT)
				&& f.getElement(2).equals(VertexFormats.UV_ELEMENT)
			) {
				if (
					f.getElement(3).equals(VertexFormats.LMAP_ELEMENT)
					|| f.getElement(3).equals(VertexFormats.NORMAL_ELEMENT)
				) {
					if (
						f.getElementCount() >= 5
							&& f.getElement(3).equals(VertexFormats.LMAP_ELEMENT)
							&& f.getElement(4).equals(VertexFormats.NORMAL_ELEMENT)
					) {
						logger.debug("[Indigo] Classified format as ShadersMod-compatible: " + format);
						return MODE_COPY_PADDED_SHADERSMOD;
					} else if (f.getElementCount() == 4) {
						logger.debug("[Indigo] Classified format as vanilla-like: " + format);
						return MODE_COPY_FAST;
					} else {
						logger.debug("[Indigo] Unsupported but likely vanilla-compliant vertex format. " + format);
						return MODE_COPY_PADDED;
					}
				}
			}

			return MODE_UNSUPPORTED;
		});
	}
}
