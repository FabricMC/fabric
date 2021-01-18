package net.fabricmc.fabric.api.client.event.input;

public class CharEvent {
	public final int codepoint;
	public final int mods;

	public CharEvent(int codepoint, int mods) {
		this.codepoint = codepoint;
		this.mods = mods;
	}

	public int getCodepoint() {
		return this.codepoint;
	}
	public int getMods() {
		return this.mods;
	}
}
