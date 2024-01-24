package net.fabricmc.fabric.impl.item;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;

import net.minecraft.item.ItemStack;

public class ShearsItemPredicate implements CustomItemPredicate {
	public static final ShearsItemPredicate INSTANCE = new ShearsItemPredicate();

	private final Codec<ShearsItemPredicate> CODEC = Codec.unit(this);

	private ShearsItemPredicate() {
	}

	@Override
	public Codec<? extends CustomItemPredicate> getCodec() {
		return CODEC;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return itemStack.isShears();
	}
}
