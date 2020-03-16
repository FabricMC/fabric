package net.fabricmc.fabric.mixin.registry.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.sync.ModdableRegistry;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;

@Mixin(MutableRegistry.class)
public abstract class MixinMutableRegistry<T> extends Registry<T> implements ModdableRegistry {
	@Unique
	private boolean modded = false;

	@Unique
	private int preBootstrapHash = 0;

	@Inject(method = "add", at = @At("RETURN"))
	private <V extends T> void add(Identifier id, V entry, CallbackInfoReturnable<V> info) {
		onChange(id);
	}

	@Inject(method = "set", at = @At("RETURN"))
	private <V extends T> void set(int rawId, Identifier id, V entry, CallbackInfoReturnable<V> info) {
		onChange(id);
	}

	@Unique
	private void onChange(Identifier id) {
		if (RegistrySyncManager.postBootstrap) {
			markModded();
		} else if (!id.getNamespace().equals("minecraft")) {
			markModded();
		}
	}

	@Override
	public boolean isModded() {
		if (preBootstrapHash != 0) {
			if (getIds().hashCode() != preBootstrapHash) {
				markModded();
			}

			preBootstrapHash = 0;
		}

		return modded;
	}

	@Override
	public void markModded() {
		modded = true;
	}

	@Override
	public void storeIdHash(int hash) {
		preBootstrapHash = hash;
	}
}
