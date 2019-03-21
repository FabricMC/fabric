/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for config
 */
public class FabricConfigManager {
	
	private static final Logger LOGGER = LogManager.getLogger();

	//masquerade as JSON5, since JSON5 is a valid subset of Jankson
	private static final String CONFIG_FILE_EXTENSION = ".json5";

	/**
	 * Loads a .config file from the config folder and parses it to a POJO.
	 *
	 * @param clazz The class of the POJO that will store all our properties
	 * @param configName The name of the config file
	 * @return A new config Object containing all our options from the config file
	 */
	public static <T> T loadConfig(Class<T> clazz, String configName){
		try {
			File file = new File((FabricLoader.getInstance()).getConfigDirectory(), configName+CONFIG_FILE_EXTENSION);
			Jankson jankson = Jankson.builder().build();

			//Generate config file if it doesn't exist
			if(!file.exists()) {
				T newConfig = clazz.newInstance();
				saveConfig(newConfig, configName);
				return newConfig;
			}

			try {
				JsonObject json = jankson.load(file);

				T result = jankson.fromJson(json, clazz);

				//check if the config file is outdated. If so, overwrite it
				JsonElement jsonElementNew = jankson.toJson(clazz.newInstance());
				if(jsonElementNew instanceof JsonObject){
					JsonObject jsonNew = (JsonObject) jsonElementNew;
					if(json.getDelta(jsonNew).size()>= 0){
						saveConfig(result, configName);
					}
				}

				return result;
			}
			catch (IOException e) {
				LOGGER.warn("Failed to load config File "+configName+CONFIG_FILE_EXTENSION+": ", e);
			}
		}
		catch (SyntaxError syntaxError) {
			LOGGER.warn("Failed to load config File "+configName+CONFIG_FILE_EXTENSION+": ", syntaxError);
		} catch (IllegalAccessException | InstantiationException e) {
			LOGGER.warn("Failed to create new config file for "+configName+CONFIG_FILE_EXTENSION+": ", e);
		}

		//Something obviously went wrong, create placeholder config
		LOGGER.warn("Creating placeholder config for "+configName+CONFIG_FILE_EXTENSION+"...");
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.warn("Failed to create placeholder config for "+configName+CONFIG_FILE_EXTENSION+": ",e);
		}

		//this is ... unfortunate
		return null;
	}

	/**
	 * Saves a POJO Config object to the disk.
	 * This is mostly used to create new configs if they don't already exist.
	 *
	 * @param object The Config we want to save
	 * @param configName The filename of our config.
	 */
	public static void saveConfig(Object object, String configName){
		Jankson jankson = Jankson.builder().build();
		JsonElement json = jankson.toJson(object);
		String result = json.toJson(true,true);


		try {
			File file = new File((FabricLoader.getInstance()).getConfigDirectory().toString()+"/"+configName+CONFIG_FILE_EXTENSION);
			if(!file.exists())
				file.createNewFile();

			FileOutputStream out = new FileOutputStream(file,false);

			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to write to config file"+configName+CONFIG_FILE_EXTENSION+": " + e);
		}
	}

}
