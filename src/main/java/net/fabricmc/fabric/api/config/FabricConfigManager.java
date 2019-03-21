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

public class FabricConfigManager {
	
	private static final Logger LOGGER = LogManager.getLogger();

	private static final String CONFIG_FILE_EXTENSION = ".conf";

	/**
	 * Loads a .config file from the config folder and parses it to a POJO.
	 *
	 * @param clazz The class of the POJO that will store all our properties
	 * @return A new config Object containing all our options from the config file
	 */
	public static <T> T loadConfig(Class<T> clazz) {
		String configName;
		if(clazz.isAnnotationPresent(ConfigFile.class)){
			configName = clazz.getAnnotation(ConfigFile.class).name();
		} else {
			configName = clazz.getSimpleName();
		}
		return loadConfig(clazz, configName);
	}

	/**
	 * Loads a .config file from the config folder and parses it to a POJO.
	 *
	 * @param clazz The class of the POJO that will store all our properties
	 * @param configName The name of the config file
	 * @return A new config Object containing all our options from the config file
	 */
	public static <T> T loadConfig(Class<T> clazz, String configName){
		try {
			File file = new File((FabricLoader.getInstance()).getConfigDirectory().toString()+"/"+configName+CONFIG_FILE_EXTENSION);
			Jankson jankson = Jankson.builder().build();

			//Generate config file if it doesn't exist
			if(!file.exists()) {
				saveConfig(clazz.newInstance(), configName);
			}

			try {
				JsonObject json = jankson.load(file);

				T result = jankson.fromJson(json, clazz);

				//check if the config file is outdated. If so, overwrite it
				JsonElement jsonElementNew = jankson.toJson(clazz.newInstance());
				if(jsonElementNew instanceof JsonObject){
					JsonObject jsonNew = (JsonObject) jsonElementNew;
					if(json.getDelta(jsonNew).size()>= 0){
						saveConfig(result);
					}
				}

				return result;
			}
			catch (IOException e) {
				("Failed to load config File "+configName+CONFIG_FILE_EXTENSION+": ", e);
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
	 * Saves a POJO Config object to the disk,
	 * and uses either the name specified in the Annotation (if available) or the simple Class name as filename.
	 * This function is used to create new configs if they don't already exist.
	 *
	 * @param object The Config we want to save
	 */
	public static void saveConfig(Object object){
		String configName;
		if(object.getClass().isAnnotationPresent(ConfigFile.class)){
			configName = object.getClass().getAnnotation(ConfigFile.class).name();
		} else {
			configName = object.getClass().getSimpleName();
		}
		saveConfig(object,configName);
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
