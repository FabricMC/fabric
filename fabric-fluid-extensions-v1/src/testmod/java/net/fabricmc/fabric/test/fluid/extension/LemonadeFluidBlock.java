package net.fabricmc.fabric.test.fluid.extension;

import net.fabricmc.fabric.api.fluid.extension.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.fluid.extension.v1.FabricFlowableFluidBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;

public class LemonadeFluidBlock extends FabricFlowableFluidBlock {
	public static Material LEMONADE_MATERIAL = new Material(MaterialColor.YELLOW, true, false, false, false, false, true, PistonBehavior.DESTROY);
	
	public LemonadeFluidBlock() {
		super(AbstractBlock.Settings.of(LEMONADE_MATERIAL).noCollision().ticksRandomly().strength(100.0F).luminance((state) -> {
			return 15;
		}).dropsNothing());
	}

	@Override
	public FabricFlowableFluid getFluid() {
		return FluidTest.LEMONADE_FLUID;
	}
}
