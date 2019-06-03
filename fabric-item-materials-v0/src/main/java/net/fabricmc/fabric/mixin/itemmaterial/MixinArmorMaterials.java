package net.fabricmc.fabric.mixin.itemmaterial;

import net.fabricmc.fabric.impl.itemmaterial.ArmorMaterialsExtensions;
import net.minecraft.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ArmorMaterials.class)
public class MixinArmorMaterials implements ArmorMaterialsExtensions {
	@Shadow
	@Final
	private static int[] BASE_DURABILITY;

	@Override
	public int[] fabric_getBaseDurabilityArray() {
		return BASE_DURABILITY;
	}
}
