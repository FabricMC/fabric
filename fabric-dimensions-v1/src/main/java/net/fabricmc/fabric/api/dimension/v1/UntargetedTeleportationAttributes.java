package net.fabricmc.fabric.api.dimension.v1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * For untargetted teleportations, the registered {@link  UntargetedTeleportationHandler} will decide how to place
 * the placer. This interface defines standard attributes that a mod requesting teleportation can pass along
 * to the handler to influence it's behavior.
 *
 * <p>The mod that implements the target dimension can choose not to handle any of these attributes, or
 * define attributes of its own.
 */
public final class UntargetedTeleportationAttributes {
	private static final UntargetedTeleportationAttributes EMPTY = new UntargetedTeleportationAttributes(Collections.emptyMap());

	private final Map<UntargetedTeleportationAttribute<?>, Object> attributes;

	private UntargetedTeleportationAttributes(Map<UntargetedTeleportationAttribute<?>, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Checks whether a given attribute is present.
	 */
	public boolean has(UntargetedTeleportationAttribute<?> attribute) {
		return attributes.containsKey(attribute);
	}

	/**
	 * Returns the value associated with a given attribute or null if that attribute is not present.
	 */
	/* Nullable */
	public <T> T get(UntargetedTeleportationAttribute<T> attribute) {
		return attribute.getValueClass().cast(attributes.get(attribute));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UntargetedTeleportationAttributes that = (UntargetedTeleportationAttributes) o;
		return attributes.equals(that.attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(attributes);
	}

	@Override
	public String toString() {
		if (attributes.isEmpty()) {
			return "EMPTY";
		}

		StringBuilder result = new StringBuilder();

		for (Map.Entry<UntargetedTeleportationAttribute<?>, Object> entry : attributes.entrySet()) {
			if (result.length() > 0) {
				result.append(',');
			}

			result.append(entry.getKey().getId())
					.append('=')
					.append(entry.getValue());
		}

		return result.toString();
	}

	/**
	 * Returns an instance that has no attributes.
	 */
	public static UntargetedTeleportationAttributes empty() {
		return EMPTY;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		// Lower expected maximum size since we assume most calls will have a low attribute call
		private final Map<UntargetedTeleportationAttribute<?>, Object> attributes = new HashMap<>(2);

		private Builder() {
		}

		public <T> Builder add(UntargetedTeleportationAttribute<T> attribute, T value) {
			this.attributes.put(attribute, value);
			return this;
		}

		public UntargetedTeleportationAttributes build() {
			if (attributes.isEmpty()) {
				return EMPTY;
			}

			return new UntargetedTeleportationAttributes(attributes);
		}
	}
}
