package net.fabricmc.fabric.api.fluids.properties;

public final class NbtIdentifier {
	public final int type;
	public final String namespace;
	public final String path;

	/**
	 * @deprecated for minecraft namespaces only!
	 */
	@Deprecated
	@SuppressWarnings ("DeprecatedIsStillUsed")
	public NbtIdentifier(String val, int type) {
		this(val, "minecraft", type);
	}

	public NbtIdentifier(String namespace, String path, int type) {
		this.type = type;
		this.namespace = namespace;
		this.path = path;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.type;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		NbtIdentifier that = (NbtIdentifier) o;
		return this.type == that.type;
	}
}
