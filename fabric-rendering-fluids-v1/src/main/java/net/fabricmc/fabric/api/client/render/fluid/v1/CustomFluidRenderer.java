package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Interface to handle custom rendering for fluids, if providing custom fluid sprites is not enough. This interface
 * allows you to tessellate your custom fluids yourself and will override any {@link FluidRenderHandler} registered for
 * your fluids.
 */
public interface CustomFluidRenderer {
	CustomFluidRenderer DEFAULT = (pos, world, vertexConsumer, state, defaultBehavior) -> defaultBehavior.drawFluidAsDefault();

	/**
	 * Render your fluid. This method will be invoked instead of the default fluid renderer ({@link FluidRenderer}), and
	 * hence overrides any {@link FluidRenderHandler}s registered for the fluid this renderer is registered for.
	 *
	 * <p>Even though the {@link FluidRenderHandler} for your fluid is not used, you can decide to manually use it by
	 * loading it from the {@link FluidRenderRegistry}.
	 *
	 * <p>Note that this method must *only* return {@code true} if at least one face is tessellated. If no faces are
	 * tessellated this method must return {@code false}.
	 *
	 * @param pos            The position in the world, of the fluid to render
	 * @param world          The world the fluid is in
	 * @param vertexConsumer The vertex consumer to tessellate the fluid in
	 * @param state          The fluid state being rendered
	 * @return Whether anything is tessellated
	 */
	boolean renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state, DefaultBehavior defaultBehavior);

	interface DefaultBehavior {
		boolean drawFluidAsDefault();
	}
}
