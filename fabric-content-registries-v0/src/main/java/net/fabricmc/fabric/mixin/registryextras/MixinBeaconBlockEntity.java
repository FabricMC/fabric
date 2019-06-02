package net.fabricmc.fabric.mixin.registryextras;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity{

	@ModifyVariable(method = "updateLevel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
	private Block checkBeaconCompatible(Block original) {
		Tag<Block> beaconBaseTag = BlockTags.getContainer().get(new Identifier("fabric", "beacon_base"));
		if (beaconBaseTag != null && beaconBaseTag.contains(original)) return Blocks.IRON_BLOCK;
		else return original;
	}
}
