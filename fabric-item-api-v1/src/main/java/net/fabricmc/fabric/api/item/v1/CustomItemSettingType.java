package net.fabricmc.fabric.api.item.v1;

import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.minecraft.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A type of setting that can be passed to item constructors.
 * This feature can be used by mods to add non-standard settings
 * to items in a way that is compatible with other mods that add
 * settings to items.
 *
 * @param <T> the type of the setting to be attached
 */
public final class CustomItemSettingType<T> {
	private final Supplier<@NotNull T> defaultValue;

	private CustomItemSettingType(Supplier<@NotNull T> defaultValue) {
		this.defaultValue = defaultValue;
	}

	// There are a lot of these for convenience sake.
	public static <T> CustomItemSettingType<T> of(Supplier<T> defaultValue) {
		return new CustomItemSettingType<>(defaultValue);
	}

	public static CustomItemSettingType<Integer> of(int defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Long> of(long defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Float> of(float defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Double> of(double defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Boolean> of(boolean defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Byte> of(byte defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Short> of(short defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<Character> of(char defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	public static CustomItemSettingType<String> of(String defaultValue) {
		return new CustomItemSettingType<>(() -> defaultValue);
	}

	@ApiStatus.Internal
	public T getDefaultValue() {
		return defaultValue.get();
	}

	/**
	 * Returns the current value of this setting for the given {@link Item.Settings}.
	 *
	 * @param settings the item stetings
	 * @return the current setting if present, the default setting if not
	 */
	public T getValue(Item.Settings settings) {
		return FabricItemInternals.computeExtraData(settings).getCustomSetting(this);
	}
}
