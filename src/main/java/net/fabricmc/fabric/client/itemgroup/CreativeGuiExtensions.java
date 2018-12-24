package net.fabricmc.fabric.client.itemgroup;

public interface CreativeGuiExtensions {

	void fabric_NextPage();

	void fabric_PreviousPage();

	boolean fabric_isButtonVisible(FabricItemGroupUtils.Type type);

	boolean fabric_isButtonEnabled(FabricItemGroupUtils.Type type);
}
