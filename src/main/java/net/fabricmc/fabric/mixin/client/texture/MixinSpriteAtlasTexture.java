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
import net.fabricmc.fabric.api.event.client.SpriteRegistrationCallback;
import net.fabricmc.fabric.client.texture.*;
import net.fabricmc.fabric.impl.client.texture.FabricSprite;
import net.minecraft.class_1050;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.transformer.meta.MixinInner;

import java.io.IOException;
import java.util.*;

@Mixin(SpriteAtlasTexture.class)
public abstract class MixinSpriteAtlasTexture {
	// TODO

	@Shadow
	private static Logger LOGGER;
	@Shadow
	@Final
	@Mutable
	private Set<Identifier> spritesToLoad;
	@Shadow
	private Map<Identifier, Sprite> sprites;
	@Shadow
	private int mipLevel;

	/*@Shadow
	public abstract Sprite getSprite(Identifier id);
	@Shadow
	public abstract void addSpriteToLoad(ResourceManager var1, Identifier var2);

	// private Collection<Sprite> method_18164(ResourceManager resourceManager_1, Set<Identifier> set_1) {
	@Redirect(method = "method_18164", at = @At(value = "NEW", target = "net/minecraft/client/texture/Sprite"))
	public Sprite newSprite(Identifier id, class_1050 c, AnimationResourceMetadata animationMetadata) {
		if (sprites.containsKey(id)) {
			return sprites.get(id);
		} else {
			return new FabricSprite(id, c, animationMetadata);
		}
	} */

	/* @Inject(at = @At("HEAD"), method = "build")
	public void build(ResourceManager var1, Iterable<Identifier> var2, CallbackInfo info) {
		this.sprites.clear();
	}

	@Inject(at = @At("HEAD"), method = "reload")
	public void reload(ResourceManager manager, CallbackInfo info) {
		SpriteRegistrationCallback.Registry registry = new SpriteRegistrationCallback.Registry(sprites, (id) -> addSpriteToLoad(manager, id));
		SpriteRegistrationCallback.EVENT.invoker().registerSprites((SpriteAtlasTexture) (Object) this, registry);

		// TODO: Unoptimized.
		Set<DependentSprite> dependentSprites = new HashSet<>();
		Set<Identifier> dependentSpriteIds = new HashSet<>();
		for (Identifier id : spritesToLoad) {
			Sprite sprite;
			if ((sprite = getSprite(id)) instanceof DependentSprite) {
				dependentSprites.add((DependentSprite) sprite);
				dependentSpriteIds.add(id);
			}
		}

		if (!dependentSprites.isEmpty()) {
			Set<Identifier> oldSpritesToLoad = spritesToLoad;
			spritesToLoad = new LinkedHashSet<>();

			for (Identifier id : oldSpritesToLoad) {
				if (!dependentSpriteIds.contains(id)) {
					spritesToLoad.add(id);
				}
			}

			int lastSpriteSize = 0;
			while (lastSpriteSize != spritesToLoad.size() && spritesToLoad.size() < oldSpritesToLoad.size()) {
				lastSpriteSize = spritesToLoad.size();

				for (DependentSprite sprite : dependentSprites) {
					Identifier id = ((Sprite) sprite).getId();
					if (!spritesToLoad.contains(id) && spritesToLoad.containsAll(sprite.getDependencies())) {
						spritesToLoad.add(id);
					}
				}
			}

			if (spritesToLoad.size() < oldSpritesToLoad.size()) {
				CrashReport report = CrashReport.create(new Throwable(), "Resolving sprite dependencies");
				for (DependentSprite sprite : dependentSprites) {
					Identifier id = ((Sprite) sprite).getId();
					if (!spritesToLoad.contains(id)) {
						CrashReportSection element = report.addElement("Unresolved sprite");
						element.add("Sprite", id);
						element.add("Dependencies", Joiner.on(',').join(sprite.getDependencies()));
					}
				}
				throw new CrashException(report);
			}
		}
	} */

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
