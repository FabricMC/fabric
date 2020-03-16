package net.fabricmc.fabric.impl.registry.sync;

public interface ModdableRegistry {
	boolean isModded();

	void markModded();

	void storeIdHash(int hash);
}
