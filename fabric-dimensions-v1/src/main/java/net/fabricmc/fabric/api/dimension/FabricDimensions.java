package net.fabricmc.fabric.api.dimension;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

/**
 * This class consists exclusively of static methods that operate on world dimensions.
 */
public final class FabricDimensions {
    private FabricDimensions() {
        throw new AssertionError();
    }

    /**
     * Teleports an entity to a different dimension, using custom placement logic.
     *
     * <p> If {@code customPlacement} is {@code null}, this method behaves as if:
     * <pre>{@code teleported.changeDimension(destination)}</pre>
     * The {@code customPlacement} may itself return {@code null}, in which case
     * the default placement logic for that dimension will be run. If {@code destination}
     * is a {@link FabricDimensionType}, that logic is {@link FabricDimensionType#getDefaultPlacement()}.
     * If {@code destination} is the nether or the overworld, the default logic is the vanilla path.
     * For any other dimension, the default placement behaviour is undefined.
     *
     * <p> After calling this method, {@code teleported} may be invalidated. Callers should use
     * the returned entity for any further manipulation.
     *
     * @param teleported      the entity to teleport
     * @param destination     the dimension the entity will be teleported to
     * @param customPlacement custom placement logic that will run before the default one,
     *                        or {@code null} for no custom placement.
     * @param <E>             the type of the teleported entity
     * @return the teleported entity, or a clone of it
     */
    public static <E extends Entity> E teleport(E teleported, DimensionType destination, /*Nullable*/ EntityPlacer customPlacement) {
        return FabricDimensionInternals.changeDimension(teleported, destination, customPlacement);
    }
}
