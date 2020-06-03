package net.fabricmc.fabric.api.util;

import java.util.Objects;

public final class NbtIdentifier {
	public final int type;
	public final String namespace;
	public final String path;

	public NbtIdentifier(String val, int type) {
		this.type = type;
		String[] splt = val.split(":");
		if(splt.length == 1) {
			this.namespace = "minecraft";
			this.path = splt[0];
		} else {
			this.namespace = splt[0];
			this.path = splt[1];
		}
	}

	public NbtIdentifier(String namespace, String path, int type) {
		if (type < 0 || type > NbtType.LONG_ARRAY) throw new IllegalArgumentException("Invalid NBT type: " + type);
		this.type = type;
		this.namespace = namespace;
		this.path = path;
	}

	@Override
	public int hashCode() {
		int result = this.type;
		result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
		result = 31 * result + (this.path != null ? this.path.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NbtIdentifier)) return false;
		NbtIdentifier that = (NbtIdentifier) o;
		return this.type == that.type && Objects.equals(this.namespace, that.namespace) && Objects.equals(this.path, that.path);
	}

	@Override
	public String toString() {
		return this.namespace + ":" + this.path + " of " + this.type;
	}
}
