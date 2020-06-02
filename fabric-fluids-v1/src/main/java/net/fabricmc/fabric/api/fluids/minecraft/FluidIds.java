package net.fabricmc.fabric.api.fluids.minecraft;

import static net.minecraft.util.registry.Registry.FLUID;

import java.util.Objects;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * the ids for all vanilla fluids
 */
public class FluidIds {
	public static final Identifier WATER = get(Fluids.WATER);
	public static final Identifier LAVA = get(Fluids.LAVA);
	public static final Identifier EMPTY = get(Fluids.EMPTY);
	/**
	 * found in honey bottles
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier HONEY = new Identifier("honey");
	/**
	 * found in splash/lingering/normal potion bottles
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier POTION = new Identifier("potion");
	/**
	 * found in mushroom stew
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier MUSHROOM_STEW = new Identifier("mushroom_stew");
	/**
	 * found in experience bottles
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier EXPERIENCE = new Identifier("enchanting");
	/**
	 * found in suspicious stew
	 * todo fluid properties, do suspicious stew store effects in NBT?
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier SUSPICIOUS_STEW = new Identifier("suspicious_stew");
	/**
	 * found in bottles of dragon's breath
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier DRAGONS_BREATH = new Identifier("dragon_breath");
	/**
	 * found in buckets of milk
	 *
	 * @see #shouldAdd(Identifier)
	 */
	public static final Identifier MILK = new Identifier("milk");

	/**
	 * when dealing with vanilla fluids (or other modded fluids) that don't have a {@link Fluid} class associated with them
	 * multiple mods may want to add the fluids, so it's recommended to check if another mod has already beat you to it or not
	 *
	 * @param id the vanilla id
	 * @return true if the mod is good to go on registering a fluid for the given id
	 */
	public static boolean shouldAdd(Identifier id) {
		return !id.equals(EMPTY) && FLUID.get(id) == Fluids.EMPTY;
	}

	/**
	 * gets the fluid for the given id
	 *
	 * @param identifier the id
	 * @return the fluid, or {@link Fluids#EMPTY} if none was found
	 */
	public static Fluid forId(Identifier identifier) {
		return Registry.FLUID.get(identifier);
	}

	public static boolean miscible(Identifier a, Identifier b) {
		return EMPTY.equals(a) || EMPTY.equals(b) || Objects.equals(a, b);
	}

	public static Identifier getId(Fluid fluid) {
		return FLUID.getId(fluid);
	}

	public static Identifier getNonEmpty(Identifier a, Identifier b) {
		return EMPTY.equals(a) ? b : a;
	}

	private static Identifier get(Fluid fluid) {
		return FLUID.getId(fluid);
	}
}
