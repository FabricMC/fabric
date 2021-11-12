package net.fabricmc.fabric.api.util;

import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Incapsulates some sound parameters.
 */
@SuppressWarnings("unused")
public class SoundParameters {
	private final SoundEvent soundEvent;
	private final float volume;
	private final float pitch;

	/**
	 * SoundParameters instance with no sound.
	 */
	private static final SoundParameters EMPTY = of(null);

	/**
	 * @return a SoundParameters instance with no sound.
	 */
	public static @NotNull SoundParameters empty() {
		return EMPTY;
	}

	/**
	 * @param soundEvent Sound to play.
	 * @return a new SoundParameters instance.
	 */
	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SoundParameters of(SoundEvent soundEvent) {
		return new SoundParameters(soundEvent, 1f, 1f);
	}

	/**
	 * @param soundEvent Sound to play.
	 * @param volume Sound volume.
	 * @param pitch Sound pitch.
	 * @return a new SoundParameters instance.
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	public static @NotNull SoundParameters of(SoundEvent soundEvent, float volume, float pitch) {
		return new SoundParameters(soundEvent, volume, pitch);
	}

	/**
	 * Initializes a new SoundParameters instance.
	 * @param soundEvent Sound to play.
	 * @param volume Sound volume.
	 * @param pitch Sound pitch.
	 */
	private SoundParameters(SoundEvent soundEvent, float volume, float pitch) {
		this.soundEvent = soundEvent;
		this.volume = volume;
		this.pitch = pitch;
	}

	/**
	 * @return the SoundEvent.
	 */
	public SoundEvent getSoundEvent() {
		return soundEvent;
	}

	/**
	 * @return the sound volume.
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * @return the sound pitch.
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @return true if the SoundEvent is not null.
	 */
	public boolean hasSound() {
		return soundEvent != null;
	}

	/**
	 * If the SoundEvent is not null, performs the given action with the value, otherwise does nothing.
	 * @param action the action to be performed, if the SoundEvent is not null.
	 * @throws NullPointerException if the SoundEvent is not null and the given action is {@code null}.
	 */
	public void ifHasSound(Consumer<SoundParameters> action) {
		if (hasSound()) action.accept(this);
	}

	/**
	 * If the SoundEvent is not null, performs the given action with the value,
	 * otherwise performs the given empty-based action.
	 * @param action the action to be performed, if the SoundEvent is not null.
	 * @param emptyAction the empty-based action to be performed, if the SoundEvent is null.
	 * @throws NullPointerException if the SoundEvent is not null and the given action is {@code null},
	 * or the SoundEvent is null and the given empty-based action is {@code null}.
	 */
	public void ifHasSoundOrElse(Consumer<SoundParameters> action, Runnable emptyAction) {
		if (hasSound()) action.accept(this);
		else emptyAction.run();
	}
}
