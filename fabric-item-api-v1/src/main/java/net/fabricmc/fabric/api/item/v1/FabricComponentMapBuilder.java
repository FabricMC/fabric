package net.fabricmc.fabric.api.item.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.minecraft.component.ComponentType;

public interface FabricComponentMapBuilder {

	@Contract("_,_->new")
	default <T> T getOrCreate(ComponentType<T> type, Supplier<T> defaultCreator) {
		throw new AssertionError("Implemented in Mixin");
	}

	@Contract("_,_->new")
	default <T> T getOrDefault(ComponentType<T> type, @Nullable T defaultValue) {
		return getOrCreate(type, () -> defaultValue);
	}

	@Contract("_->new")
	default <T> List<T> getOrEmpty(ComponentType<List<T>> type)  {
		throw new AssertionError("Implemented in Mixin");
	}

}
