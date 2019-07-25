package net.fabricmc.fabric.api.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Fabric version of DimensionType.
 * DimensionType is a registry wrapper for Dimension.
 * Stores a default {@link EntityPlacer} which is used to place entities in the world when they join.
 * Instances of this class get their raw ids automatically assigned.
 */
public class FabricDimensionType extends DimensionType {
    private final Identifier identifier;
    private final EntityPlacer defaultPlacement;
    private int fixedId;

    /**
     * @param name          a unique name used both for the dimension suffix and the save directory
     * @param factory       a function creating new {@code Dimension} instances
     * @param defaultPlacer a default {@code EntityPlacer} for the dimension
     * @param hasSkyLight   {@code true} if the dimension type should have skylight like the overworld,
     *                      {@code false} otherwise
     * @throws NullPointerException if any of the parameters is null
     */
    public FabricDimensionType(Identifier name, BiFunction<World, DimensionType, ? extends Dimension> factory, EntityPlacer defaultPlacer, boolean hasSkyLight) {
        // Pass an arbitrary raw id that does not map to any vanilla dimension. That id should never get used.
        super(3, name.getNamespace() + "_" + name.getPath(), "DIM_" + name.getNamespace() + "_" + name.getPath(), factory, hasSkyLight);
        Objects.requireNonNull(factory);
        this.identifier = name;
        this.defaultPlacement = Objects.requireNonNull(defaultPlacer);
    }

    /**
     * Sets this dimension's fixed id, replacing the vanilla raw dimension id.
     *
     * @param fixedId the new raw id for this dimension type
     * @apiNote Mods that used to have a dimension with a manually set id
     * may use this method to set a default id corresponding to the old one,
     * so as not to break compatibility with old worlds.
     */
    public void setFixedId(int fixedId) {
        this.fixedId = fixedId;
    }

    /**
     * Return the current raw id for this dimension type.
     * The returned id is guaranteed to be unique and persistent in a save,
     * as well as synchronized between a server and its connected clients.
     *
     * @return the current raw id for this dimension type
     * @implNote the raw id may change when connecting to a different server
     * or opening a new save.
     * @see #setFixedId(int)
     */
    @Override
    public int getRawId() {
        if (this.fixedId == 0) {
            return Registry.DIMENSION.getRawId(this) + -1;
        }
        return this.fixedId;
    }

    /**
     * Return the identifier used to name this dimension. The identifier may be reused for registration.
     *
     * @return the identifier used to name this dimension
     */
    public Identifier getUniqueName() {
        return identifier;
    }

    /**
     * Return the default placement logic for this dimension. The returned placer
     * never returns {@code null} when called.
     *
     * @return the default placement logic for this dimension
     * @see FabricDimensions#teleport(Entity, DimensionType, EntityPlacer)
     */
    public EntityPlacer getDefaultPlacement() {
        return this.defaultPlacement;
    }
}
