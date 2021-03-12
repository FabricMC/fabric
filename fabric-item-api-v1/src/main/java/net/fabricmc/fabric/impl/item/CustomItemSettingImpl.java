package net.fabricmc.fabric.impl.item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.CustomItemSetting;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;

public class CustomItemSettingImpl<T> implements CustomItemSetting<T> {
	public static final CustomItemSetting<EquipmentSlotProvider> EQUIPMENT_SLOT_PROVIDER = CustomItemSetting.create(() -> null);
	public static final CustomItemSetting<CustomDamageHandler> CUSTOM_DAMAGE_HANDLER = CustomItemSetting.create(() -> null);

	private static final Map<Item.Settings, Collection<CustomItemSettingImpl<?>>> CUSTOM_SETTINGS = new WeakHashMap<>();

	private final Map<Item.Settings, T> customSettings = new WeakHashMap<>();
	private final Map<Item, T> customItemSettings = new HashMap<>();
	private final Supplier<T> defaultValue;

	public CustomItemSettingImpl(Supplier<T> defaultValue) {
		Objects.requireNonNull(defaultValue);

		this.defaultValue = defaultValue;
	}

	@Override
	public T getValue(Item item) {
		Objects.requireNonNull(item);

		return this.customItemSettings.computeIfAbsent(item, i -> this.defaultValue.get());
	}

	public void set(Item.Settings settings, T value) {
		this.customSettings.put(settings, value);
		CUSTOM_SETTINGS.computeIfAbsent(settings, s -> new HashSet<>()).add(this);
	}

	private void build(Item.Settings settings, Item item) {
		this.customItemSettings.put(item, this.customSettings.getOrDefault(settings, this.defaultValue.get()));
	}

	public static void onBuild(Item.Settings settings, Item item) {
		for (CustomItemSettingImpl<?> setting : CUSTOM_SETTINGS.getOrDefault(settings, Collections.emptyList())) {
			setting.build(settings, item);
		}
	}
}
