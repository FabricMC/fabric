package net.fabricmc.fabric.api.dimension.v1;

import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;

public final class UntargetedTeleportationAttribute<T> {
	private final String id;

	private final Class<T> valueClass;

	private UntargetedTeleportationAttribute(String id, Class<T> valueClass) {
		Preconditions.checkNotNull(id, "id must not be null");
		Preconditions.checkNotNull(valueClass, "valueClass must not be null");
		Preconditions.checkArgument(id.toLowerCase(Locale.ROOT).equals(id), "id must be lowercase");
		this.id = id;
		this.valueClass = valueClass;
	}

	/**
	 * The id given to this attribute. Always lowercase.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the class of values associated with this attribute.
	 */
	public Class<T> getValueClass() {
		return valueClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UntargetedTeleportationAttribute<?> that = (UntargetedTeleportationAttribute<?>) o;
		return id.equals(that.id)
				&& valueClass.equals(that.valueClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, valueClass);
	}
}
