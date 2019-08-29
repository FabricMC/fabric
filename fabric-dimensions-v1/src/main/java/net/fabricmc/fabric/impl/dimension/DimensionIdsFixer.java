package net.fabricmc.fabric.impl.dimension;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.dimension.FabricDimensionType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.impl.registry.RemapException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Handles fixing raw dimension ids between saves and servers,
 * and synchronizes said ids.
 */
public class DimensionIdsFixer {
	private static final Field FABRIC_DIMENSION_TYPE$RAW_ID;
    static final Identifier ID = new Identifier("fabric", "dimension/sync");

    /**
     * Assigns a unique id to every registered {@link FabricDimensionType}, keeping the known ids
     * from {@code savedIds}.
     * @param savedIds a compound tag mapping dimension ids to raw ids
     * @return id to raw id mappings of the current instance
     * @throws RemapException if dimensions IDs conflict irredeemably
     */
    public static CompoundTag apply(CompoundTag savedIds) throws RemapException {
        /*
         * We want to give to each fabric dimension a unique ID. We also want to give back previously assigned ids.
         * And we have to take into account non-fabric dimensions, which raw IDs cannot change.
         * So we iterate over every dimension, note the ones which id cannot change, then update the free ones.
         */
        Int2ObjectMap<Identifier> fixedIds = new Int2ObjectOpenHashMap<>();
        List<FabricDimensionType> fabricDimensions = new ArrayList<>();
        CompoundTag fabricDimensionIds = new CompoundTag();
        // step 1: detect all fabric and non-fabric dimensions
        for (Identifier id : Registry.DIMENSION.getIds()) {
            DimensionType dimensionType = Objects.requireNonNull(DimensionType.byId(id));
            if (dimensionType instanceof FabricDimensionType) {
				FabricDimensionType fabricDimension = (FabricDimensionType) dimensionType;
				fabricDimensions.add(fabricDimension);
				// reset the fixed raw id to the preferred raw id
                setFixedRawId(fabricDimension, fabricDimension.getDesiredRawId());
            } else {
                Identifier existing = fixedIds.put(dimensionType.getRawId(), id);
                if (existing != null) {
                    throw new RemapException("Two non-fabric dimensions have the same raw dim id (" + dimensionType.getRawId() + ") : " + existing + " and " + id);
                }
            }
        }
        // step 2: read saved ids
        for (String key : savedIds.getKeys()) {
            int savedRawId = savedIds.getInt(key);
            Identifier dimId = new Identifier(key);
            Identifier existing = fixedIds.putIfAbsent(savedRawId, dimId);
            if (existing != null && !existing.equals(dimId)) {
                throw new RemapException("Saved fabric dimension got replaced with a non-fabric one! " + dimId + " replaced with " + existing + " (raw id: " + savedRawId + ")");
            }
            DimensionType dim = DimensionType.byId(dimId);
            if (dim instanceof FabricDimensionType) {
            	setFixedRawId((FabricDimensionType) dim, savedRawId);
            } else {
                FabricDimensionInternals.LOGGER.warn("A saved dimension has {}: {}", dim == null ? "been removed" : "stopped using the dimensions API", dimId);
                // Preserve saved ids in case the mod is eventually added back
                fabricDimensionIds.putInt(dimId.toString(), savedRawId);
            }
        }
        // step 3: de-duplicate raw ids for dimensions which ids are not fixed yet
        int nextFreeId = 0;
        for (FabricDimensionType fabricDimension : fabricDimensions) {
            int rawDimId = fabricDimension.getRawId();
            Identifier dimId = Objects.requireNonNull(DimensionType.getId(fabricDimension));
            if (fixedIds.containsKey(rawDimId) && !fixedIds.get(rawDimId).equals(dimId)) {
                while (fixedIds.containsKey(nextFreeId)) ++nextFreeId;
                setFixedRawId(fabricDimension, nextFreeId);
                rawDimId = nextFreeId;
            }
            fixedIds.put(rawDimId, dimId);
            fabricDimensionIds.putInt(dimId.toString(), rawDimId);
        }
        return fabricDimensionIds;
    }

	/**
	 * Reflectively set the fixed raw id on a {@link FabricDimensionType}.
	 * @see FabricDimensionType#getRawId()
	 */
	private static void setFixedRawId(FabricDimensionType fabricDimension, int rawId) {
		try {
			FABRIC_DIMENSION_TYPE$RAW_ID.setInt(fabricDimension, rawId);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to fix a raw id on a FabricDimensionType", e);
		}
	}

	public static Packet<?> createPacket(LevelProperties levelProperties) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeCompoundTag(((DimensionIdsHolder) levelProperties).fabric_getDimensionIds());
        return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
    }

    static void receivePacket(PacketContext context, PacketByteBuf buf, Consumer<Exception> errorHandler) {
        CompoundTag compound = buf.readCompoundTag();
        try {
            context.getTaskQueue().executeFuture(() -> {
                if (compound == null) {
                    errorHandler.accept(new RemapException("Received null compound tag in dimension sync packet!"));
                    return null;
                }
                try {
                    apply(compound);
                } catch (RemapException e) {
                    errorHandler.accept(e);
                }
                return null;
            }).get(30, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            errorHandler.accept(e);
        }
    }

    static {
		try {
			FABRIC_DIMENSION_TYPE$RAW_ID = FabricDimensionType.class.getDeclaredField("fixedRawId");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
