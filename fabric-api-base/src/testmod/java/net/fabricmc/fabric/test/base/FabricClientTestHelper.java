package net.fabricmc.fabric.test.base;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import net.fabricmc.fabric.test.base.mixin.CyclingButtonWidgetAccessor;
import net.fabricmc.fabric.test.base.mixin.ScreenAccessor;
import net.fabricmc.loader.api.FabricLoader;

// Provides thread safe utils for interacting with a running game.
public final class FabricClientTestHelper {
	public static void waitForLoadingComplete() {
		waitFor("Loading to complete", client -> client.getOverlay() == null);
	}

	public static void waitForScreen(Class<? extends Screen> screenClass) {
		waitFor("Screen %s".formatted(screenClass.getName()), client -> client.currentScreen != null && client.currentScreen.getClass() == screenClass);
	}

	public static void openGameMenu() {
		submit(client -> {
			client.setScreen(new GameMenuScreen(true));
			return null;
		});

		waitForScreen(GameMenuScreen.class);
	}

	public static void takeScreenshot(String name) {
		submitAndWait(client -> {
			ScreenshotRecorder.saveScreenshot(FabricLoader.getInstance().getGameDir().toFile(), name + ".png", client.getFramebuffer(), (message) -> {
			});
			return null;
		});
	}

	public static void clickScreenButton(String translationKey) {
		final String buttonText = Text.translatable(translationKey).getString();

		waitFor("Click button" + buttonText, client -> {
			final Screen screen = client.currentScreen;

			if (screen == null) {
				return false;
			}

			final ScreenAccessor screenAccessor = (ScreenAccessor) screen;

			for (Drawable drawable : screenAccessor.getDrawables()) {
				if (drawable instanceof PressableWidget pressableWidget && pressMatchingButton(pressableWidget, buttonText)) {
					return true;
				}

				if (drawable instanceof GridWidget gridWidget) {
					for (Element child : gridWidget.children()) {
						if (child instanceof PressableWidget pressableWidget && pressMatchingButton(pressableWidget, buttonText)) {
							return true;
						}
					}
				}
			}

			// Was unable to find the button to press
			return false;
		});
	}

	private static boolean pressMatchingButton(PressableWidget widget, String text) {
		if (widget instanceof ButtonWidget buttonWidget) {
			if (text.equals(buttonWidget.getMessage().getString())) {
				buttonWidget.onPress();
				return true;
			}
		}

		if (widget instanceof CyclingButtonWidget<?> buttonWidget) {
			CyclingButtonWidgetAccessor accessor = (CyclingButtonWidgetAccessor) buttonWidget;

			if (text.equals(accessor.getOptionText().getString())) {
				buttonWidget.onPress();
				return true;
			}
		}

		return false;
	}

	public static void waitForWorldTicks(long ticks) {
		// Wait for the world to be loaded and get the start ticks
		waitFor("World load", client -> client.world != null, Duration.ofMinutes(30));
		final long startTicks = submitAndWait(client -> client.world.getTime());
		waitFor("World load", client -> Objects.requireNonNull(client.world).getTime() > startTicks + ticks, Duration.ofMinutes(10));
	}

	private static void waitFor(String what, Predicate<MinecraftClient> predicate) {
		waitFor(what, predicate, Duration.ofSeconds(10));
	}

	private static void waitFor(String what, Predicate<MinecraftClient> predicate, Duration timeout) {
		final LocalDateTime end = LocalDateTime.now().plus(timeout);

		while (true) {
			boolean result = submitAndWait(predicate::test);

			if (result) {
				break;
			}

			if (LocalDateTime.now().isAfter(end)) {
				throw new RuntimeException("Timed out waiting for " + what);
			}

			waitFor(Duration.ofSeconds(1));
		}
	}

	private static void waitFor(Duration duration) {
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> CompletableFuture<T> submit(Function<MinecraftClient, T> function) {
		return MinecraftClient.getInstance().submit(() -> function.apply(MinecraftClient.getInstance()));
	}

	private static <T> T submitAndWait(Function<MinecraftClient, T> function) {
		return submit(function).join();
	}
}
