package net.fabricmc.fabric.impl.resource.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Language;

import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ServerLanguageUtil {
	private static final String ASSETS_PREFIX = ResourceType.CLIENT_RESOURCES.getDirectory() + '/';

	public static Collection<Path> getModLanguageFiles() {
		Set<Path> paths = new HashSet<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (mod.getMetadata().getType().equals("builtin")) continue;

			final Map<ResourceType, Set<String>> map = ModNioResourcePack.readNamespaces(mod.getRootPaths(), mod.getMetadata().getId());
			for (String ns : map.get(ResourceType.CLIENT_RESOURCES)) {
				mod.findPath(ASSETS_PREFIX + ns + "/lang/" + Language.DEFAULT_LANGUAGE + ".json")
						.filter(Files::isRegularFile)
						.ifPresent(paths::add);
			}
		}
		return paths;
	}
}
