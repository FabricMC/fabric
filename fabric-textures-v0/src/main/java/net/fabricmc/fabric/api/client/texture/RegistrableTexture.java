package net.fabricmc.fabric.api.client.texture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

/**
 * Contains a texture identifier and a parameter that specifies if the texture is to be registered.
 */
public class RegistrableTexture {
	/**
	 * Contains the identifier of the texture.
	 */
	protected Identifier identifier;

	/**
	 * Specifies if the texture is to be registered.
	 */
	protected boolean toBeRegistered;

	/**
	 * Returns an empty {@link RegistrableTexture}.
	 * <p>NOTE: The identifier is null, and the texture is marked as to not be registered.</p>
	 */
	public static @NotNull RegistrableTexture empty() {
		return new RegistrableTexture(null, false);
	}

	/**
	 * Returns a {@link RegistrableTexture} from the texture id.
	 * <p>NOTE: The texture is marked as to be registered.</p>
	 *
	 * @param id The id of the texture.
	 */
	public static @NotNull RegistrableTexture registrable(String id) {
		return new RegistrableTexture(new Identifier(id), true);
	}

	/**
	 * Returns a {@link RegistrableTexture} from the texture namespace and path.
	 * <p>NOTE: The texture is marked as to be registered.</p>
	 *
	 * @param namespace The namespace of the texture.
	 * @param path      The namespace of the texture.
	 */
	public static @NotNull RegistrableTexture registrable(String namespace, String path) {
		return new RegistrableTexture(new Identifier(namespace, path), true);
	}

	/**
	 * Returns a {@link RegistrableTexture} from the texture identifier.
	 * <p>NOTE: The texture is marked as to be registered.</p>
	 *
	 * @param identifier The identifier of the texture.
	 */
	public static @NotNull RegistrableTexture registrable(Identifier identifier) {
		return new RegistrableTexture(identifier, true);
	}

	/**
	 * Returns a {@link RegistrableTexture} from the texture id.
	 * <p>NOTE: The texture is marked as to not be registered.</p>
	 *
	 * @param id The id of the texture.
	 */
	public static @NotNull RegistrableTexture nonRegistrable(String id) {
		return new RegistrableTexture(new Identifier(id), false);
	}

	/**
	 * Returns a {@link RegistrableTexture} from the texture namespace and path.
	 * <p>NOTE: The texture is marked as to not be registered.</p>
	 *
	 * @param namespace The namespace of the texture.
	 * @param path      The namespace of the texture.
	 */
	public static @NotNull RegistrableTexture nonRegistrable(String namespace, String path) {
		return new RegistrableTexture(new Identifier(namespace, path), false);
	}

	/**
	 * Returns a {@link RegistrableTexture} from the texture identifier.
	 * <p>NOTE: The texture is marked as to not be registered.</p>
	 *
	 * @param identifier The identifier of the texture.
	 */
	public static @NotNull RegistrableTexture nonRegistrable(Identifier identifier) {
		return new RegistrableTexture(identifier, false);
	}

	/**
	 * Instantiates a new {@link RegistrableTexture}
	 * specifying the texture identifier and a parameter that specifies
	 * if the texture is to be registered or not.
	 *
	 * @param identifier     The identifier of the texture.
	 * @param toBeRegistered Specifies if the texture must be registered.
	 */
	protected RegistrableTexture(Identifier identifier, boolean toBeRegistered) {
		this.identifier = identifier;
		this.toBeRegistered = toBeRegistered;
	}

	/**
	 * Returns the texture identifier.
	 */
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * Returns true if the texture is to be registered, false otherwise.
	 */
	public boolean isToBeRegistered() {
		return toBeRegistered;
	}

	/**
	 * Returns true if the instance does not contain any texture identifier, false otherwise.
	 */
	public boolean isEmpty() {
		return identifier == null;
	}
}
