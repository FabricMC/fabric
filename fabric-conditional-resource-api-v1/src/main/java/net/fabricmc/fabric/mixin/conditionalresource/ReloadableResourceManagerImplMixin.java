package net.fabricmc.fabric.mixin.conditionalresource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.conditionalresource.v1.ResourceConditions;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
	@Shadow
	public abstract Resource getResource(Identifier id) throws IOException;

	@Shadow
	@Final
	private static Logger LOGGER;
	@Unique
	private Map<Identifier, Boolean> hidden = new ConcurrentHashMap<>();

	@Inject(method = "findResources", at = @At("RETURN"))
	private void findResources(String resourceType, Predicate<String> pathPredicate, CallbackInfoReturnable<Collection<Identifier>> cir) {
		Collection<Identifier> resourceIds = cir.getReturnValue();
		resourceIds.removeIf(this::isHiddenCached);
	}

	@Inject(method = "clear", at = @At("RETURN"))
	private void clear(CallbackInfo ci) {
		this.hidden.clear();
	}

	@Unique
	private boolean isHiddenCached(Identifier resourceId) {
		if (resourceId.getPath().endsWith(".fabricmeta")) {
			return false;
		}

		{
			Identifier folder = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring(0, resourceId.getPath().lastIndexOf('/') + 1));

			Boolean hiddenBool = hidden.get(folder);

			if (hiddenBool != null) {
				if (hiddenBool) {
					return true;
				}
			} else {
				boolean hidden = isHidden(folder);
				this.hidden.put(folder, hidden);

				if (hidden) {
					LOGGER.info("hide " + folder);
				}

				if (hidden) {
					return true;
				}
			}
		}

		Boolean hiddenBool = hidden.get(resourceId);

		if (hiddenBool != null) {
			return hiddenBool;
		}

		boolean hidden = isHidden(resourceId);
		this.hidden.put(resourceId, hidden);

		if (hidden) {
			LOGGER.info("hide " + resourceId);
		}

		return hidden;
	}

	@Unique
	private boolean isHidden(Identifier resourceId) {
		Identifier fabricMeta = new Identifier(resourceId.getNamespace(), resourceId.getPath() + ".fabricmeta");

		try (Resource resource = getResource(fabricMeta)) {
			JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream()));
			JsonElement element = Streams.parse(reader);

			try {
				return !ResourceConditions.evaluate(resourceId, element);
			} catch (Exception e) {
				LOGGER.error("Failed to evaluate conditions for {}: {}", resourceId, element);
			}
		} catch (FileNotFoundException ignored) {
			// Ignored
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
