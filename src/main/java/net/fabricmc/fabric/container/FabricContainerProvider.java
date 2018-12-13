package net.fabricmc.fabric.container;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

/**
 * This is an extension of vanilla's ContainerProvider that simplifies the implementation while keeping it compatible
 */
public interface FabricContainerProvider extends ContainerProvider {

	@Override
	default String getContainerId() {
		return getContainerIdentifier().toString();
	}

	@Override
	default boolean method_16914() {
		return false;
	}

	@Override
	default TextComponent getName() {
		return new TranslatableTextComponent(getContainerIdentifier() + ".name");
	}

	@Override
	default Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return createContainer(playerEntity);
	}

	/**
	 * Return the container to be opened on the server
	 * @param playerEntity the player
	 * @return the container to open on the player
	 */
	Container createContainer(PlayerEntity playerEntity);

	/**
	 *
	 * @return A unique id for this container, this must be the same as the id used when registering the gui/container handler
	 */
	Identifier getContainerIdentifier();
}
