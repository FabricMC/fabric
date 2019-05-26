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

package net.fabricmc.indigo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.indigo.renderer.IndigoRenderer;
import net.fabricmc.indigo.renderer.aocalc.AoConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class Indigo implements ClientModInitializer {
	public static final boolean ALWAYS_TESSELATE_INDIGO;
	public static final AoConfig AMBIENT_OCCLUSION_MODE;
	private static final Logger LOGGER = LogManager.getLogger();

	private static boolean asBoolean(String property, boolean defValue) {
		switch (asTriState(property)) {
			case TRUE:
				return true;
			case FALSE:
				return false;
			default:
				return defValue;
		}
	}

	private static <T extends Enum> T asEnum(String property, T defValue) {
		if (property == null || property.isEmpty()) {
			return defValue;
		} else {
			for (Enum obj : defValue.getClass().getEnumConstants()) {
				if (property.equalsIgnoreCase(obj.name())) {
					//noinspection unchecked
					return (T) obj;
				}
			}

			return defValue;
		}
	}

	private static TriState asTriState(String property) {
		if (property == null || property.isEmpty()) {
			return TriState.DEFAULT;
		} else {
			switch (property.toLowerCase(Locale.ROOT)) {
				case "true":
					return TriState.TRUE;
				case "false":
					return TriState.FALSE;
				case "auto":
				default:
					return TriState.DEFAULT;
			}
		}
	}

	static {
		File configDir = new File(FabricLoader.getInstance().getConfigDirectory(), "fabric");
		if (!configDir.exists()) {
			if (!configDir.mkdir()) {
				LOGGER.warn("[Indigo] Could not create configuration directory: " + configDir.getAbsolutePath());
			}
		}

		File configFile = new File(configDir, "indigo-renderer.properties");
		Properties properties = new Properties();
		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (IOException e) {
				LOGGER.warn("[Indigo] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		ALWAYS_TESSELATE_INDIGO = asBoolean((String) properties.computeIfAbsent("always-tesselate-blocks", (a) -> "auto"), true);
		AMBIENT_OCCLUSION_MODE = asEnum((String) properties.computeIfAbsent("ambient-occlusion-mode", (a) -> "enhanced"), AoConfig.ENHANCED);

		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			properties.store(stream, "Indigo properties file");
		} catch (IOException e) {
			LOGGER.warn("[Indigo] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

    @Override
    public void onInitializeClient() {
    	if (IndigoMixinConfigPlugin.shouldApplyIndigo()) {
		    LOGGER.info("[Indigo] Registering Indigo renderer!");
		    RendererAccess.INSTANCE.registerRenderer(IndigoRenderer.INSTANCE);
	    } else {
    		LOGGER.info("[Indigo] Different rendering plugin detected; not applying Indigo.");
	    }
    }
}
