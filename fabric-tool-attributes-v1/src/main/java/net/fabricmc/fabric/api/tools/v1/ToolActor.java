package net.fabricmc.fabric.api.tools.v1;

import javax.annotation.Nullable;

/**
 * Interface for the user of a tool.
 * @param <T> The type of actor that will use this tool.
 */
public interface ToolActor<T> {
	ToolActor<Void> NO_ACTOR = () -> null;

	static <T> ToolActor<T> of(T actor) {
		return () -> actor;
	}

	/**
	 * @return The actor using a tool, if they exist.
	 */
	@Nullable
	T get();
}
