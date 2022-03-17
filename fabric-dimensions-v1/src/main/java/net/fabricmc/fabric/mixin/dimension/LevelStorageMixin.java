package net.fabricmc.fabric.mixin.dimension;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * After removing a dimension mod or a dimension datapack, Minecraft may fail to enter
 * the world, because it fails to deserialize the chunk generator of the custom dimensions in file {@code level.dat}
 * This mixin will remove the custom dimensions from the nbt tag, so the deserializer and DFU cannot see custom dimensions and won't cause errors.
 * The custom dimensions will be re-added later.
 * <p>
 * This Mixin changes a vanilla behavior that is deemed as a bug (MC-197860). In vanilla, the custom dimension is not removed after uninstalling the dimension datapack.
 * This makes custom dimensions non-removable. Most players don't want this behavior.
 * With this Mixin, custom dimensions will be removed when its datapack is removed.
 */
@Mixin(LevelStorage.class)
public class LevelStorageMixin {
	@SuppressWarnings("unchecked")
	@Inject(method = "readGeneratorProperties", at = @At("HEAD"))
	private static <T> void onReadGeneratorProperties(
			Dynamic<T> nbt, DataFixer dataFixer, int version,
			CallbackInfoReturnable<Pair<GeneratorOptions, Lifecycle>> cir
	) {
		NbtElement nbtTag = ((Dynamic<NbtElement>) nbt).getValue();

		if (nbtTag instanceof NbtCompound compoundTag) {
			NbtCompound worldGenSettings = ((NbtCompound) nbtTag).getCompound("WorldGenSettings");

			removeNonVanillaDimensionsFromWorldGenSettingsTag(worldGenSettings);
		}
	}

	/**
	 * Removes all non-vanilla dimensions from the tag. The custom dimensions will be re-added later from the datapacks.
	 */
	@Unique
	private static void removeNonVanillaDimensionsFromWorldGenSettingsTag(NbtCompound worldGenSettings) {
		String[] vanillaDimensionIds =
				new String[]{"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"};

		NbtCompound dimensions = worldGenSettings.getCompound("dimensions");

		if (dimensions.getSize() > vanillaDimensionIds.length) {
			NbtCompound newDimensions = new NbtCompound();
			for (String dimId : vanillaDimensionIds) {
				if (dimensions.contains(dimId)) {
					newDimensions.put(dimId, dimensions.getCompound(dimId));
				}
			}

			worldGenSettings.put("dimensions", newDimensions);
		}
	}
}
