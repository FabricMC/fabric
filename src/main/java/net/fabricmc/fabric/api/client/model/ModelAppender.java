package net.fabricmc.fabric.api.client.model;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;

import java.util.function.Consumer;

@FunctionalInterface
public interface ModelAppender {
	void append(ResourceManager manager, Consumer<ModelIdentifier> modelAdder);
}
