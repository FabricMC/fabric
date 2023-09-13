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

package net.fabricmc.fabric.impl.tag.convention;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;

public class ConventionLogWarningConfigs {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConventionLogWarningConfigs.class);

	/**
	 * A config option mainly for developers.
	 * Logs out modded item tags that do not have translations when running on integrated server.
	 * Defaults to DEV_SHORT.
	 */
	public static final LOG_WARNING_MODES LOG_UNTRANSLATED_WARNING_MODE;
	/**
	 * A config option mainly for developers.
	 * Logs out modded tags that are using the 'c' namespace when running on integrated server or dedicated server.
	 * Defaults to DEV_SHORT.
	 */
	public static final LOG_WARNING_MODES LOG_LEGACY_WARNING_MODE;

	public enum LOG_WARNING_MODES {
		SILENCED,
		DEV_SHORT,
		DEV_VERBOSE,
		PROD_SHORT,
		PROD_VERBOSE
	}

	static {
		File configDir = FabricLoader.getInstance().getConfigDir().resolve("fabric").toFile();

		if (!configDir.exists()) {
			if (!configDir.mkdir()) {
				LOGGER.warn("[Fabric Tag Conventions v1] Could not create configuration directory: " + configDir.getAbsolutePath());
			}
		}

		File configFile = new File(configDir, "fabric-tag-conventions-v1.properties");
		Properties properties = new Properties();

		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (IOException e) {
				LOGGER.warn("[Fabric Tag Conventions v1] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		LOG_UNTRANSLATED_WARNING_MODE = asEnum((String) properties.computeIfAbsent("log-untranslated-item-tag-warnings", (a) -> "SILENCED"), LOG_WARNING_MODES.SILENCED);
		LOG_LEGACY_WARNING_MODE = asEnum((String) properties.computeIfAbsent("log-legacy-tag-warnings", (a) -> "DEV_SHORT"), LOG_WARNING_MODES.DEV_SHORT);

		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			properties.store(stream, "Fabric Tag Conventions v1 properties file");
		} catch (IOException e) {
			LOGGER.warn("[Fabric Tag Conventions v1] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
}
