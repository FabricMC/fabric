/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Represents a boolean value which can be true, false or refer to a default value.
 */
public enum TriState {
	/**
	 * Represents the boolean value of {@code false}.
	 */
	FALSE,
	/**
	 * Represents a value that refers to a "default" value, often as a fallback.
	 */
	DEFAULT,
	/**
	 * Represents the boolean value of {@code true}.
	 */
	TRUE;

	/**
	 * Gets the corresponding tri-state from a boolean value.
	 *
	 * @param bool the boolean value
	 * @return {@link TriState#TRUE} or {@link TriState#FALSE} depending on the value of the boolean.
	 */
	public static TriState of(boolean bool) {
		return bool ? TRUE : FALSE;
	}

	/**
	 * Gets a tri-state from a nullable boxed boolean.
	 *
	 * @param bool the boolean value
	 * @return {@link TriState#DEFAULT} if {@code null}.
	 * Otherwise {@link TriState#TRUE} or {@link TriState#FALSE} depending on the value of the boolean.
	 */
	public static TriState of(/* @Nullable */ Boolean bool) {
		if (bool == null) {
			return DEFAULT;
		}

		return TriState.of(bool.booleanValue());
	}

	/**
	 * Gets the value of the tri-state.
	 *
	 * @return true if the tri-state is {@link TriState#TRUE}.
	 * Otherwise false.
	 */
	public boolean get() {
		return this == TRUE;
	}

	/**
	 * Gets the value of the tri-state as a boxed, nullable boolean.
	 *
	 * @return {@code null} if {@link TriState#DEFAULT}.
	 * Otherwise {@code true} if {@link TriState#TRUE} or {@code false} if {@link TriState#FALSE}.
	 */
	/* @Nullable */
	public Boolean getBoxed() {
		if (this == TriState.DEFAULT) {
			return null;
		}

		return this.get();
	}

	/**
	 * Gets the value of this tri-state.
	 * If the value is {@link TriState#DEFAULT} then use the supplied value.
	 *
	 * @param value the value to fallback to
	 * @return the value of the tri-state or the supplied value if {@link TriState#DEFAULT}.
	 */
	public boolean orElse(boolean value) {
		return this == TriState.DEFAULT ? value : this.get();
	}

	/**
	 * Gets the value of this tri-state.
	 * If the value is {@link TriState#DEFAULT} then use the supplied value.
	 *
	 * @param supplier the supplier used to get the value to fallback to
	 * @return the value of the tri-state or the value of the supplier if the tri-state is {@link TriState#DEFAULT}.
	 */
	public boolean orElseGet(BooleanSupplier supplier) {
		return this == TriState.DEFAULT ? supplier.getAsBoolean() : this.get();
	}

	/**
	 * Maps the value of an optional if this tri-state is {@link TriState#TRUE}.
	 *
	 * @param mapper the mapper to use
	 * @param <T> the type of object supplied by the mapper
	 * @return an optional containing the value if {@link TriState#TRUE} and the value returned by the mapper is non-null, otherwise {@link Optional#empty()}.
	 */
	public <T> Optional<T> mapTrue(Supplier<T> mapper) {
		Objects.requireNonNull(mapper);

		if (this == TRUE) {
			return Optional.ofNullable(mapper.get());
		}

		return Optional.empty();
	}

	/**
	 * Maps the value of an optional if this tri-state is {@link TriState#TRUE}.
	 *
	 * @param mapper the mapper to use
	 * @param <T> the type of object supplied by the mapper
	 * @return an optional containing the value if {@link TriState#FALSE} and the value returned by the mapper is non-null, otherwise {@link Optional#empty()}.
	 */
	public <T> Optional<T> mapFalse(Supplier<T> mapper) {
		Objects.requireNonNull(mapper);

		if (this == FALSE) {
			return Optional.ofNullable(mapper.get());
		}

		return Optional.empty();
	}

	/**
	 * Maps the value of an optional if this tri-state is {@link TriState#DEFAULT}.
	 *
	 * @param mapper the mapper to use
	 * @param <T> the type of object supplied by the mapper
	 * @return an optional containing the value if {@link TriState#DEFAULT} and the value returned by the mapper is non-null, otherwise {@link Optional#empty()}.
	 */
	public <T> Optional<T> mapDefault(Supplier<T> mapper) {
		Objects.requireNonNull(mapper);

		if (this == DEFAULT) {
			return Optional.ofNullable(mapper.get());
		}

		return Optional.empty();
	}

	/**
	 * Calls the supplied runnable if this tri-state is {@link TriState#TRUE}.
	 *
	 * @param runnable the interface to run
	 */
	public void ifTrue(Runnable runnable) {
		if (this == TRUE) {
			runnable.run();
		}
	}

	/**
	 * Calls the supplied runnable if this tri-state is {@link TriState#FALSE}.
	 *
	 * @param runnable the interface to run
	 */
	public void ifFalse(Runnable runnable) {
		if (this == FALSE) {
			runnable.run();
		}
	}

	/**
	 * Calls the supplied runnable if this tri-state is {@link TriState#DEFAULT}.
	 *
	 * @param runnable the interface to run
	 */
	public void ifDefault(Runnable runnable) {
		if (this == DEFAULT) {
			runnable.run();
		}
	}

	/**
	 * Gets the value of this tri-state, or throws an exception if this tri-state's value is {@link TriState#DEFAULT}.
	 *
	 * @param exceptionSupplier the supplying function that produces an xception to be thrown
	 * @param <X> Type of the exception to be thrown
	 * @return the value
	 * @throws X if the value is {@link TriState#DEFAULT}
	 */
	public <X extends Throwable> boolean orElseThrow(Supplier<X> exceptionSupplier) throws X {
		if (this != DEFAULT) {
			return this.get();
		}

		throw exceptionSupplier.get();
	}
}
