package net.fabricmc.fabric.api.tool.attribute.v1;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tool.attribute.ToolLevels;

@FunctionalInterface
public interface ToolLevel extends Comparable<ToolLevel> {
	/**
	 * Creates a tool level from {@code level}.
	 * It is recommended that you use {@link #by(Identifier)} to allow others to edit your level.
	 *
	 * @param level the level
	 * @return the {@link ToolLevel} instance.
	 */
	static ToolLevel of(float level) {
		return ToolLevels.of(level);
	}

	/**
	 * Creates a tool level by the id, to be loaded by data packs.
	 * The tool levels are voted by the data packs, the last one loaded in wins if there is more than 1 winner.
	 *
	 * @param id the identifier of the level
	 * @return the {@link ToolLevel} instance that will be loaded afterwards.
	 */
	static ToolLevel by(Identifier id) {
		return by(id, ToolLevel.NONE);
	}

	/**
	 * Creates a tool level by the id, to be loaded by data packs.
	 * The tool levels are voted by the data packs, the last one loaded in wins if there is more than 1 winner.
	 *
	 * @param id       the identifier of the level
	 * @param fallback the fallback of the level if it is not voted
	 * @return the {@link ToolLevel} instance that will be loaded afterwards.
	 */
	static ToolLevel by(Identifier id, @NotNull ToolLevel fallback) {
		return ToolLevels.by(id, fallback);
	}

	ToolLevel NONE = of(-1.0F);
	ToolLevel MINIMUM = of(0);
	ToolLevel WOOD = ToolMaterials.WOOD::getMiningLevel;
	ToolLevel STONE = ToolMaterials.STONE::getMiningLevel;
	ToolLevel IRON = ToolMaterials.IRON::getMiningLevel;
	ToolLevel DIAMOND = ToolMaterials.DIAMOND::getMiningLevel;
	ToolLevel GOLD = ToolMaterials.GOLD::getMiningLevel;
	ToolLevel NETHERITE = ToolMaterials.NETHERITE::getMiningLevel;

	float getLevel();

	@Override
	default int compareTo(@NotNull ToolLevel o) {
		return Float.compare(getLevel(), o.getLevel());
	}
}
