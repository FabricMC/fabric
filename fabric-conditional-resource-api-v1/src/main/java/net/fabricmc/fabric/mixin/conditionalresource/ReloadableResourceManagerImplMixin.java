package net.fabricmc.fabric.mixin.conditionalresource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;

import net.fabricmc.fabric.impl.conditionalresource.NamespaceResourceManagerExtensions;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
	@Shadow
	@Final
	private Map<String, NamespaceResourceManager> namespaceManagers;

	@Inject(method = "beginReloadInner", at = @At("HEAD"))
	private void beginReloadInner(Executor prepareExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Unit> initialStage, CallbackInfoReturnable<ResourceReloadMonitor> cir) {
		for (NamespaceResourceManager manager : namespaceManagers.values()) {
			((NamespaceResourceManagerExtensions) manager).fabric_indexFabricMeta();
		}
	}
}
