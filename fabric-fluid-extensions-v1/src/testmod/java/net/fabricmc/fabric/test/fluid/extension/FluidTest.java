package net.fabricmc.fabric.test.fluid.extension;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.fluid.extension.v1.FabricFlowableFluidBlock;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidTest implements ModInitializer {
	public static String MOD_ID = "fabric-fluid-extensions-v1-testmod";
	
	public static LemonadeFluid LEMONADE_FLUID;
	public static BucketItem LEMONADE_BUCKET;
	public static FabricFlowableFluidBlock LEMONADE_BLOCK;
	
	@Override
	public void onInitialize() {
		LEMONADE_FLUID = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "lemonade"), new LemonadeFluid());
		LEMONADE_BUCKET = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "lemonade_bucket"), new BucketItem(LEMONADE_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
		LEMONADE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "lemonade"), new LemonadeFluidBlock());
	}
}
