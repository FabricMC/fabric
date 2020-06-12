package net.fabricmc.fabric.api.structures.v0;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * A general class that provides guaranteed extension of key methods
 */
public abstract class FabricStructure<T extends FeatureConfig> extends StructureFeature<T> {
	public FabricStructure(Codec<T> codec) {
		super(codec);
	}

	/**
	 * The helper methods mean this method ought to be the original definition of the name for a structure
	 *
	 * Is overridden to add namespaces to the names where possible
	 */
	@Override
	public String getName() {
		return  Registry.STRUCTURE_FEATURE.stream().anyMatch((structure) -> structure == this) ? Registry.STRUCTURE_FEATURE.getId(this).toString() : super.getName();
	}

	/**
	 * A method to easily allow structures to define what generation step they generate in
	 *
	 * @return The generation step the structure ought to generate in
	 */
	public abstract GenerationStep.Feature generationStep();

	/**
	 * Is overridden to remove the circular convoluted logic that previously defined structure generation steps
	 */
	@Override
	public final GenerationStep.Feature method_28663() {
		return generationStep();
	}
}
