package net.fabricmc.fabric.api.tools;

import com.sun.istack.internal.Nullable;

/**
 * Interface for the user of a tool.
 * @param <T> The type of actor that will use this tool.
 */
public interface ToolActor<T> {
	ToolActor NO_ACTOR = () -> null;

	/**
	 * @return The actor using a tool, if they exist.
	 */
	@Nullable
	T getActor();
}
