package net.fabricmc.fabric.api.fluid.v1;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Implements the basic behaviour of every fluid, plus some basic extended behaviour, by implementing ExtendedFlowableFluid.
 */
public abstract class ExtendedFabricFlowableFluid extends FabricFlowableFluid implements ExtendedFlowableFluid {
	/**
	 * @return true if the fluid can extinguish fire.
	 */
	@Override
	public boolean canExtinguishFire() {
		return true;
	}

	/**
	 * @return true if the fluid can prevent fall damage.
	 */
	@Override
	public boolean canPreventFallDamage() {
		return true;
	}

	/**
	 * Event executed when an entity falls, or enters, into the fluid.
	 *
	 * @param world  The current world.
	 * @param pos    The position of the current entity.
	 * @param entity The current entity in the fluid.
	 */
	@Override
	public void onSplash(World world, Vec3d pos, Entity entity) {}

	/**
	 * Event executed when the entity is into the fluid.
	 *
	 * @param world  The current world.
	 * @param entity The current entity in the fluid.
	 */
	@Override
	public void onSubmerged(World world, Entity entity) {
		//Implements the drowning for every living entity, you can customize by overriding
		if (entity instanceof LivingEntity life) {
			life.setAir(life.getAir() - 1);
			if (life.getAir() == -20) {
				life.setAir(0);
				life.damage(DamageSource.DROWN, 2f);
			}
		}
	}
}
