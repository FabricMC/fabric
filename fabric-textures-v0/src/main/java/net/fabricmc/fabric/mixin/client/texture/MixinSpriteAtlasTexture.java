/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.client.texture;

import com.google.common.base.Joiner;
import net.fabricmc.fabric.api.client.texture.*;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.impl.client.texture.FabricSprite;
import net.fabricmc.fabric.impl.client.texture.SpriteAtlasTextureHooks;
import net.fabricmc.fabric.impl.client.texture.SpriteRegistryCallbackHolder;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;

@Mixin(SpriteAtlasTexture.class)
public abstract class MixinSpriteAtlasTexture implements SpriteAtlasTextureHooks {
	@Unique
	private static Logger FABRIC_LOGGER = LogManager.getLogger();
	@Shadow
	private int mipLevel;

	@Shadow
	public abstract Sprite getSprite(Identifier id);

	private final Set<Identifier> fabric_localIds = new HashSet<>();

	// EVENT/HOOKS LOGIC

	@Override
	public void onRegisteredAs(Identifier id) {
		fabric_localIds.add(id);
	}

	// INJECTION LOGIC

	private Map<Identifier, Sprite> fabric_injectedSprites;

	// Loads in custom sprite object injections.
	@Inject(at = @At("RETURN"), method = "loadSprites")
	private void afterLoadSprites(ResourceManager resourceManager_1, Set<Identifier> set_1, CallbackInfoReturnable<Collection<Sprite>> info) {
		if (fabric_injectedSprites != null) {
			info.getReturnValue().addAll(fabric_injectedSprites.values());
			fabric_injectedSprites = null;
		}
	}

	// Handles DependentSprite + custom sprite object injections.
	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;"), method = "stitch")
	public Set<Identifier> beforeSpriteLoad(Set<Identifier> set) {
		fabric_injectedSprites = new HashMap<>();
		ClientSpriteRegistryCallback.Registry registry = new ClientSpriteRegistryCallback.Registry(fabric_injectedSprites, set::add);
		//noinspection ConstantConditions
		for (Identifier id : fabric_localIds) {
			SpriteRegistryCallbackHolder.eventLocal(id).invoker().registerSprites((SpriteAtlasTexture) (Object) this, registry);
		}
		SpriteRegistryCallbackHolder.EVENT_GLOBAL.invoker().registerSprites((SpriteAtlasTexture) (Object) this, registry);

		// TODO: Unoptimized.
		Set<DependentSprite> dependentSprites = new HashSet<>();
		Set<Identifier> dependentSpriteIds = new HashSet<>();
		for (Identifier id : set) {
			Sprite sprite;
			if ((sprite = fabric_injectedSprites.get(id)) instanceof DependentSprite) {
				dependentSprites.add((DependentSprite) sprite);
				dependentSpriteIds.add(id);
			}
		}

		Set<Identifier> result = set;
		boolean isResultNew = false;

		if (!dependentSprites.isEmpty()) {
			if (!isResultNew) {
				result = new LinkedHashSet<>();
				isResultNew = true;
			}

			for (Identifier id : set) {
				if (!dependentSpriteIds.contains(id)) {
					result.add(id);
				}
			}

			int lastSpriteSize = 0;
			while (lastSpriteSize != result.size() && result.size() < set.size()) {
				lastSpriteSize = result.size();

				for (DependentSprite sprite : dependentSprites) {
					Identifier id = ((Sprite) sprite).getId();
					if (!result.contains(id) && result.containsAll(sprite.getDependencies())) {
						result.add(id);
					}
				}
			}

			if (result.size() < set.size()) {
				CrashReport report = CrashReport.create(new Throwable(), "Resolving sprite dependencies");
				for (DependentSprite sprite : dependentSprites) {
					Identifier id = ((Sprite) sprite).getId();
					if (!result.contains(id)) {
						CrashReportSection element = report.addElement("Unresolved sprite");
						element.add("Sprite", id);
						element.add("Dependencies", Joiner.on(',').join(sprite.getDependencies()));
					}
				}
				throw new CrashException(report);
			}
		}

		if (!fabric_injectedSprites.isEmpty()) {
			if (!isResultNew) {
				result = new LinkedHashSet<>(set);
				isResultNew = true;
			}

			result.removeAll(fabric_injectedSprites.keySet());
		}

		return result;
	}

	/**
	 * Handles CustomSpriteLoader.
	 */
	@Inject(at = @At("HEAD"), method = "loadSprite", cancellable = true)
	public void loadSprite(ResourceManager manager, Sprite sprite, CallbackInfoReturnable<Boolean> info) {
		// refer SpriteAtlasTexture.loadSprite
		if (sprite instanceof CustomSpriteLoader) {
			try {
				if (!((CustomSpriteLoader) sprite).load(manager, mipLevel)) {
					info.setReturnValue(false);
					return;
				}
			} catch (RuntimeException | IOException e) {
				FABRIC_LOGGER.error("Unable to load custom sprite {}: {}", sprite.getId(), e);
				info.setReturnValue(false);
				return;
			}

			try {
				sprite.generateMipmaps(this.mipLevel);
				info.setReturnValue(true);
			} catch (Throwable e) {
				FABRIC_LOGGER.error("Unable to apply mipmap to custom sprite {}: {}", sprite.getId(), e);
				info.setReturnValue(false);
			}
		}
	}
}
