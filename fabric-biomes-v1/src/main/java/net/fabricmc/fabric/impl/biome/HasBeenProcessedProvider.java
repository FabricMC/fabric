package net.fabricmc.fabric.impl.biome;

// Best interface name ever
public interface HasBeenProcessedProvider {
	boolean hasBeenProcessed();

	void setProcessed();
}
