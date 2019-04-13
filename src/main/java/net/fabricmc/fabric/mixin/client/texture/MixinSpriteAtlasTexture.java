/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;

@Mixin(SpriteAtlasTexture.class)
public abstract class MixinSpriteAtlasTexture {
	@Shadow
	private static Logger LOGGER;
	@Shadow
	private int mipLevel;

	@Shadow
	public abstract Sprite getSprite(Identifier id);

	private Map<Identifier, Sprite> fabric_injectedSprites;

	/**
	 * The purpose of this patch is to allow injecting sprites at the stage of Sprite instantiation, such as
	 * Sprites with CustomSpriteLoaders.
	 *
	 * FabricSprite is a red herring. It's only use to go around Sprite's constructors being protected.
	 *
	 * method_18160 is a lambda used in runAsync.
	 */
	@SuppressWarnings("JavaDoc")
	@Redirect(method = "method_18161", at = @At(value = "NEW", target = "net/minecraft/client/texture/Sprite"))
	public Sprite newSprite(Identifier id, PngFile pngFile, AnimationResourceMetadata animationMetadata) {
		if (fabric_injectedSprites.containsKey(id)) {
			return fabric_injectedSprites.get(id);
		} else {
			return new FabricSprite(id, pngFile, animationMetadata);
		}
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;"), method = "stitch")
	public Set<Identifier> setHook(Set<Identifier> set) {
		fabric_injectedSprites = new HashMap<>();
		ClientSpriteRegistryCallback.Registry registry = new ClientSpriteRegistryCallback.Registry(fabric_injectedSprites, set::add);
		//noinspection ConstantConditions
		ClientSpriteRegistryCallback.EVENT.invoker().registerSprites((SpriteAtlasTexture) (Object) this, registry);

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

		if (!dependentSprites.isEmpty()) {
			Set<Identifier> result = new LinkedHashSet<>();

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

			return result;
		} else {
			return set;
		}
	}

	@Inject(at = @At("HEAD"), method = "loadSprite", cancellable = true)
	public void loadSprite(ResourceManager manager, Sprite sprite, CallbackInfoReturnable<Boolean> info) {
		// refer SpriteAtlasTexture.loadSprite
		if (sprite instanceof CustomSpriteLoader) {
			try {
				if (!((CustomSpriteLoader) sprite).load(manager, mipLevel)) {
					info.setReturnValue(false);
					info.cancel();
					return;
				}
			} catch (RuntimeException | IOException e) {
				LOGGER.error("Unable to load custom sprite {}: {}", sprite.getId(), e);
				info.setReturnValue(false);
				info.cancel();
				return;
			}

			try {
				sprite.generateMipmaps(this.mipLevel);
				info.setReturnValue(true);
				info.cancel();
			} catch (Throwable e) {
				LOGGER.error("Unable to apply mipmap to custom sprite {}: {}", sprite.getId(), e);
				info.setReturnValue(false);
				info.cancel();
			}
		}
	}
}
