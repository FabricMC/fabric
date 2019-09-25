package net.fabricmc.fabric.mixin.resources;

import net.fabricmc.fabric.impl.resources.CustomImageResourcePackInfo;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;

@Mixin(ClientResourcePackContainer.class)
public abstract class ClientResourcePackContainerMixin implements CustomImageResourcePackInfo {
	@Shadow
	private NativeImage icon;

	@Override
	public void setImage(ResourcePack pack, String imagePath) {
		if (this.icon != null)
			return;

		try (InputStream inputStream_1 = pack.openRoot(imagePath)) {
			this.icon = NativeImage.read(inputStream_1);
		} catch (IllegalArgumentException | IOException ignored) {
		}
	}
}
