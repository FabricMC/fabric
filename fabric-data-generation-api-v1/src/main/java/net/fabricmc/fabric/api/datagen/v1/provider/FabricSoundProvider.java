package net.fabricmc.fabric.api.datagen.v1.provider;

import java.io.IOException;
import java.util.function.BiConsumer;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Extend this class and implement {@link FabricSoundProvider#generateSounds(BiConsumer)}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class FabricSoundProvider implements DataProvider {
	private final FabricDataGenerator dataGenerator;

	public FabricSoundProvider(FabricDataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	public abstract void generateSounds(BiConsumer<Identifier, SoundEvent> soundEventConsumer);

	@Override
	public void run(DataWriter writer) throws IOException {

	}

	@Override
	public String getName() {
		return "Sounds";
	}
}
