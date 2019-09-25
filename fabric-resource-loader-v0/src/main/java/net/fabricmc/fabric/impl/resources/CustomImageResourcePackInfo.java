package net.fabricmc.fabric.impl.resources;

import net.minecraft.resource.ResourcePack;

public interface CustomImageResourcePackInfo {

	/**
	 * Sets the mod resource pack icon.
	 */
	void setImage(ResourcePack pack, String imagePath);
}
