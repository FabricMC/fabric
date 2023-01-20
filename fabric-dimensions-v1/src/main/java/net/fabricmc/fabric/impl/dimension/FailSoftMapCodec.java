package net.fabricmc.fabric.impl.dimension;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Has the same functionality as {@link UnboundedMapCodec}.
 * But it will fail-soft when an entry cannot be deserialized.
 */
public class FailSoftMapCodec<K, V> implements BaseMapCodec<K, V>, Codec<Map<K, V>> {
	public static final Logger LOGGER = LoggerFactory.getLogger("FailSoftMapCodec");

	private final Codec<K> keyCodec;
	private final Codec<V> elementCodec;

	public FailSoftMapCodec(final Codec<K> keyCodec, final Codec<V> elementCodec) {
		this.keyCodec = keyCodec;
		this.elementCodec = elementCodec;
	}

	@Override
	public Codec<K> keyCodec() {
		return keyCodec;
	}

	@Override
	public Codec<V> elementCodec() {
		return elementCodec;
	}

	@Override
	public <T> DataResult<Pair<Map<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
		return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
	}

	@Override
	public <T> DataResult<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final T prefix) {
		return encode(input, ops, ops.mapBuilder()).build(prefix);
	}

	/**
	 * In {@link BaseMapCodec#decode(DynamicOps, MapLike)},
	 * the whole deserialization will fail if one element fails.
	 * `apply2stable` will return fail when any of the two elements is failed.
	 * In this implementation, if one deserialization fails, it will log and ignore.
	 */
	@Override
	public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
		final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

		input.entries().forEach(pair -> {
			try {
				final DataResult<K> k = keyCodec().parse(ops, pair.getFirst());
				final DataResult<V> v = elementCodec().parse(ops, pair.getSecond());

				k.get().ifRight(kPartialResult -> {
					LOGGER.error("Failed to decode key {} from {}  {}", k, pair, kPartialResult);
				});
				v.get().ifRight(vPartialResult -> {
					LOGGER.error("Failed to decode value {} from {}  {}", v, pair, vPartialResult);
				});

				if (k.get().left().isPresent() && v.get().left().isPresent()) {
					builder.put(k.get().left().get(), v.get().left().get());
				} else {
					// ignore failure
				}
			} catch (Throwable e) {
				LOGGER.error("Decoding {}", pair, e);
			}
		});

		final Map<K, V> elements = builder.build();

		return DataResult.success(elements);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final FailSoftMapCodec<?, ?> that = (FailSoftMapCodec<?, ?>) o;
		return Objects.equals(keyCodec, that.keyCodec) && Objects.equals(elementCodec, that.elementCodec);
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyCodec, elementCodec);
	}

	@Override
	public String toString() {
		return "FailSoftMapCodec[" + keyCodec + " -> " + elementCodec + ']';
	}
}
