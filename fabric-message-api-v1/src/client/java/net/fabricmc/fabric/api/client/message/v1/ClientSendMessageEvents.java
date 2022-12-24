package net.fabricmc.fabric.api.client.message.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Contains client-side events triggered when sending messages.
 */
public final class ClientSendMessageEvents {
	private ClientSendMessageEvents() {
	}

	/**
	 * An event triggered when the client is about to send a chat message,
	 * typically from a client GUI. Mods can use this to block the message.
	 *
	 * <p>If a listener returned {@code false}, the message will not be sent,
	 * the remaining listeners will not be called (if any), and
	 * {@link #CHAT_CANCELED} will be triggered instead of {@link #CHAT}.
	 */
	public static final Event<AllowChat> ALLOW_CHAT = EventFactory.createArrayBacked(AllowChat.class, listeners -> (message) -> {
		for (AllowChat listener : listeners) {
			if (!listener.allowSendChatMessage(message)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * An event triggered when the client is about to send a command,
	 * which is whenever the player executes a command
	 * including client commands registered with {@code fabric-command-api}.
	 * Mods can use this to block the message.
	 * The command string does not include a slash at the beginning.
	 *
	 * <p>If a listener returned {@code false}, the command will not be sent,
	 * the remaining listeners will not be called (if any), and
	 * {@link #COMMAND_CANCELED} will be triggered instead of {@link #COMMAND}.
	 */
	public static final Event<AllowCommand> ALLOW_COMMAND = EventFactory.createArrayBacked(AllowCommand.class, listeners -> (command) -> {
		for (AllowCommand listener : listeners) {
			if (!listener.allowSendCommandMessage(command)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * An event triggered when the client sends a chat message,
	 * typically from a client GUI. Is not called when {@linkplain
	 * #ALLOW_CHAT chat messages are blocked}.
	 */
	public static final Event<Chat> CHAT = EventFactory.createArrayBacked(Chat.class, listeners -> (message) -> {
		for (Chat listener : listeners) {
			listener.onSendChatMessage(message);
		}
	});

	/**
	 * An event triggered when the client sends a command,
	 * which is whenever the player executes a command
	 * including client commands registered with {@code fabric-command-api}.
	 * Is not called when {@linkplain #ALLOW_COMMAND command messages are blocked}.
	 * The command string does not include a slash at the beginning.
	 */
	public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class, listeners -> (command) -> {
		for (Command listener : listeners) {
			listener.onSendCommandMessage(command);
		}
	});

	/**
	 * An event triggered when sending a chat message is canceled with {@link #ALLOW_CHAT}.
	 */
	public static final Event<ChatCanceled> CHAT_CANCELED = EventFactory.createArrayBacked(ChatCanceled.class, listeners -> (message) -> {
		for (ChatCanceled listener : listeners) {
			listener.onSendChatMessageCanceled(message);
		}
	});

	/**
	 * An event triggered when sending a command is canceled with {@link #ALLOW_COMMAND}.
	 * The command string does not include a slash at the beginning.
	 */
	public static final Event<CommandCanceled> COMMAND_CANCELED = EventFactory.createArrayBacked(CommandCanceled.class, listeners -> (command) -> {
		for (CommandCanceled listener : listeners) {
			listener.onSendCommandMessageCanceled(command);
		}
	});

	@FunctionalInterface
	public interface AllowChat {
		/**
		 * Called when the client is about to send a chat message,
		 * typically from a client GUI. Returning {@code false}
		 * prevents the message from being sent, and
		 * {@link #CHAT_CANCELED} will be triggered instead of {@link #CHAT}.
		 *
		 * @param message the message that will be sent to the server
		 * @return {@code true} if the message should be sent, otherwise {@code false}
		 */
		boolean allowSendChatMessage(String message);
	}

	@FunctionalInterface
	public interface AllowCommand {
		/**
		 * Called when the client is about to send a command,
		 * which is whenever the player executes a command
		 * including client commands registered with {@code fabric-command-api}.
		 * Returning {@code false} prevents the command from being sent, and
		 * {@link #COMMAND_CANCELED} will be triggered instead of {@link #COMMAND}.
		 * The command string does not include a slash at the beginning.
		 *
		 * @param command the command that will be sent to the server, without a slash at the beginning.
		 * @return {@code true} if the command should be sent, otherwise {@code false}
		 */
		boolean allowSendCommandMessage(String command);
	}

	@FunctionalInterface
	public interface Chat {
		/**
		 * Called when the client sends a chat message,
		 * typically from a client GUI. Is not called when {@linkplain
		 * #ALLOW_CHAT chat messages are blocked}.
		 *
		 * @param message the message that is being sent to the server
		 */
		void onSendChatMessage(String message);
	}

	@FunctionalInterface
	public interface Command {
		/**
		 * Called when the client sends a command,
		 * which is whenever the player executes a command
		 * including client commands registered with {@code fabric-command-api}.
		 * Is not called when {@linkplain #ALLOW_COMMAND command messages are blocked}.
		 * The command string does not include a slash at the beginning.
		 *
		 * @param command the command that is being sent to the server, without a slash at the beginning.
		 */
		void onSendCommandMessage(String command);
	}

	@FunctionalInterface
	public interface ChatCanceled {
		/**
		 * Called when sending a chat message is canceled with {@link #ALLOW_CHAT}.
		 *
		 * @param message the message that is canceled from being sent to the server
		 */
		void onSendChatMessageCanceled(String message);
	}

	@FunctionalInterface
	public interface CommandCanceled {
		/**
		 * Called when sending a command is canceled with {@link #ALLOW_COMMAND}.
		 * The command string does not include a slash at the beginning.
		 *
		 * @param command the command that is being sent to the server, without a slash at the beginning.
		 */
		void onSendCommandMessageCanceled(String command);
	}
}
