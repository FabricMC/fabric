package net.fabricmc.fabric.impl.client;

import java.nio.DoubleBuffer;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.FabricKeyboard;
import net.fabricmc.fabric.api.client.FabricMouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class FabricMouseImpl implements FabricMouse {
	public static FabricMouseImpl INSTANCE = new FabricMouseImpl();

	private double x = 0.0;
	private double y = 0.0;
	private int buttons = 0;
	private int mods = 0;
	private final DoubleBuffer fabric_x = DoubleBuffer.allocate(1);
	private final DoubleBuffer fabric_y = DoubleBuffer.allocate(1);

	private FabricMouseImpl() {
	}

	@Override
	public double impl_getX() {
		return this.x;
	}
	@Override
	public double impl_getY() {
		return this.y;
	}
	@Override
	public int impl_getPressedButtons() {
		return this.buttons;
	}
	@Override
	public boolean impl_isButtonPressed(int button) {
		return (this.buttons & (1 << button)) != 0;
	}
	@Override
	public int impl_getMods() {
		return this.mods;
	}

	public void update() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null)
			return;
		Window window = client.getWindow();
		GLFW.glfwGetCursorPos(window.getHandle(), fabric_x, fabric_y);
		this.buttons = 0;
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_1);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_2);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_3);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_4) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_4);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_5) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_5);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_6) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_6);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_7) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_7);
		if (GLFW.glfwGetMouseButton(window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_8) == GLFW.GLFW_PRESS)
			this.buttons |= (1 << GLFW.GLFW_MOUSE_BUTTON_8);
		this.mods = FabricKeyboard.getMods();
	}

}
