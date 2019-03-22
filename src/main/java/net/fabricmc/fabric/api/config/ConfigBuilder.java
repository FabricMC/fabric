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
import net.fabricmc.fabric.impl.config.ConfigManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Config managing system. Uses Falkreon's Jankson, which is a superset of both JSON and JSON5, and partial superset of HJSON.
 */
public class ConfigBuilder {
	private String modId;
	public static final String CONFIG_SUFFIX = ".json5";

	/**
	 * The list of configs for a builder.
	 */
	private static Map<String, Object> configs = new HashMap<>();

	//True when only one config file is being used, then defaults to /config/modid.json5
	private boolean singleConfig = false;

	private ConfigBuilder(String modId) {
		//will throw if config already exists
		ConfigManager.addConfig(modId, this);
		this.modId = modId;
	}

	/**
	 * Create a new builder. Throws if a config builder already exists.
	 *
	 * @param modId the ID of the mod to create a builder for.
	 * @return The builder for said mod.
	 */
	public static ConfigBuilder builder(String modId){
		return new ConfigBuilder(modId);
	}

	/**
	 *
	 * Used to get a basic config file for a mod
	 *
	 * @return the instance of the config
	 */
	public <T> T getConfig(Class<T> clazz){
		T config = getConfig(clazz, "config", false);
		singleConfig = true;
		return config;
	}

	/**
	 *
	 * Used to get a config file for a mod based on a subfolder
	 *
	 * @param clazz the config class
	 * @param name the name of the specific config file to get from
	 * @return the instance of the config
	 */
	public <T> T getConfig(Class<T> clazz, String name){
		return getConfig(clazz, name, true);
	}

	private  <T> T getConfig(Class<T> clazz, String name, boolean subDir){
		if(singleConfig){
			//Crash if a mod tries to create a sub dir config after it already has done so
			throw new UnsupportedOperationException("Config builder created a single config, cannot create more. Specify config name");
		}
		try {
			File configFile = new File("config/" + (subDir ? modId : ""), name + CONFIG_SUFFIX);
			//TODO: decide whether we want to require a constructor or not
			T config = clazz.newInstance();
			if (!configFile.exists()) {
				saveConfig(config, configFile);
			} else {
				Jankson jankson = Jankson.builder().build();
				try {
					JsonObject json = jankson.load(configFile);

					T result = jankson.fromJson(json, clazz);

					//check if the config file is outdated. If so, overwrite it with saved values
					JsonElement jsonElementNew = jankson.toJson(clazz.newInstance());
					if(jsonElementNew instanceof JsonObject){
						JsonObject jsonNew = (JsonObject) jsonElementNew;
						if(json.getDelta(jsonNew).size()>= 0){
							saveConfig(result, configFile);
						}
					}

					return result;
				} catch (SyntaxError | IOException e) {
					throw new RuntimeException("Failed to parse config file");
				}
			}
			configs.put(name, config);
			return config;
		} catch (InstantiationException | IllegalAccessException /*| InvocationTargetException | NoSuchMethodException*/ e) {
			throw new RuntimeException("Failed to load config class", e);
		}
	}

	private void saveConfig(Object config, File file) {
		Jankson jankson = Jankson.builder().build();
		JsonElement json = jankson.toJson(config);
		String result = json.toJson(true,true);

		try {
			if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
			if(!file.exists())
				file.createNewFile();

			FileOutputStream out = new FileOutputStream(file,false);

			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to save config file");
		}
	}
}
