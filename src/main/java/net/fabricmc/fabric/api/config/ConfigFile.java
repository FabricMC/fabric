package net.fabricmc.fabric.api.config;

import java.lang.annotation.*;

/**
 * Annotates a config file with the name it should have on the disk.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigFile {
	String value();
}
