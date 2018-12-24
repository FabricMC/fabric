package net.fabricmc.fabric.client.itemgroup;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

//TODO is utils the best name for this?
public class FabricItemGroupUtils {

	public static class FabricItemGroupButtonWidget extends ButtonWidget {

		CreativeGuiExtensions extensions;
		Type type;

		public FabricItemGroupButtonWidget(int id, int x, int y, Type type, CreativeGuiExtensions extensions) {
			super(id, x, y, 10, 11, type.text);
			this.extensions = extensions;
			this.type = type;
		}

		@Override
		public void onPressed(double double_1, double double_2) {
			super.onPressed(double_1, double_2);
			type.clickConsumer.accept(extensions);
		}

		@Override
		public void draw(int int_1, int int_2, float float_1) {
			this.visible = extensions.fabric_isButtonVisible(type);
			this.enabled = extensions.fabric_isButtonEnabled(type);

			super.draw(int_1, int_2, float_1);
		}
	}

	public enum Type {

		NEXT(">", CreativeGuiExtensions::fabric_NextPage),
		PREVIOUS("<", CreativeGuiExtensions::fabric_PreviousPage);

		String text;
		Consumer<CreativeGuiExtensions> clickConsumer;

		Type(String text, Consumer<CreativeGuiExtensions> clickConsumer) {
			this.text = text;
			this.clickConsumer = clickConsumer;
		}
	}


}
