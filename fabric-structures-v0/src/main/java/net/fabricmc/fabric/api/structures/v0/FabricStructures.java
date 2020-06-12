package net.fabricmc.fabric.api.structures.v0;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * API that hooks into the internal structure generation step logic
 */
public final class FabricStructures {
	private FabricStructures() { }

	/**
	 * Adds a structure to the given generation step
	 *
	 * @param structure The structure to add to the step map. Must not be null
	 */
	public static void setGenerationStep(FabricStructure structure) {
		// Put if absent was used to avoid vanilla overrides
		StructureFeature.STRUCTURE_TO_GENERATION_STEP.putIfAbsent(structure, structure.generationStep());
	}

	/**
	 * Appends a structure to the internal structure hashmap for serialization
	 *
	 * Now if users want to change the name of their structure
	 *
	 * @param structure The structure to be appended
	 */
	public static void appendStructure(FabricStructure structure) {
		StructureFeature.STRUCTURES.put(structure.getName(), structure);
	}

	/**
	 * Registers a structure in the structure registry, the serialization hashmap, and generation step hashmap
	 *
	 * @param id        The identifier under which to register the structure universally
	 * @param structure The structure to register under the given identifier
	 * @return
	 */
	public static <T extends FeatureConfig> StructureFeature<T> register(Identifier id, FabricStructure<T> structure) {
		appendStructure(structure);
		setGenerationStep(structure);
		return Registry.register(Registry.STRUCTURE_FEATURE, id, structure);
	}
}
