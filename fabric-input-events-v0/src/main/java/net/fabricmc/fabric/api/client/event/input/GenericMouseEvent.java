package net.fabricmc.fabric.api.client.event.input;

public abstract class GenericMouseEvent {
	public final double cursorX;
	public final double cursorY;
	public final double cursorDeltaX;
	public final double cursorDeltaY;
	public final int pressedButtons;
	public final int pressedMods;
	public final double scrollX;
	public final double scrollY;

	public GenericMouseEvent(double x, double y, double dx, double dy, int buttons, int mods, double scrollX, double scrollY) {
		this.cursorX = x;
		this.cursorY = y;
		this.cursorDeltaX = dx;
		this.cursorDeltaY = dy;
		this.pressedButtons = buttons;
		this.pressedMods = mods;
		this.scrollX = scrollX;
		this.scrollY = scrollY;
	}
	public GenericMouseEvent(double x, double y, double dx, double dy, int buttons, int mods) {
		this(x, y, dx, dy, buttons, mods, 0.0, 0.0);
	}
}
