package net.fabricmc.fabric.api.provider.v1;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class Example {
	interface Boop {
		void boop();
	}

	static final Boop NO_BOOP_FOR_YOU = () -> {};

	@FunctionalInterface
	interface BoopProvider extends ApiProvider<BoopProvider, Boop> {
		Boop getBoop(Direction side, PlayerEntity player);

		@Override
		default Boop getApi() {
			return NO_BOOP_FOR_YOU;
		}
	}

	static final BoopProvider HAVE_A_BOOP_MAYBE = (side, player) -> (side == Direction.UP && player.isSneaking()) ? () -> System.out.println("Boop! Side = " + side.toString()) : NO_BOOP_FOR_YOU;

	public static final BlockApiProviderAccess<BoopProvider, Boop> BOOP_ACCESS = BlockApiProviderAccess.registerAcess(new Identifier("boop:boop"), Boop.class, (side, player) -> NO_BOOP_FOR_YOU);

	static {
		BOOP_ACCESS.registerProviderForBlock((world, pos, blockState) -> HAVE_A_BOOP_MAYBE, Blocks.COBBLESTONE);
	}
}
