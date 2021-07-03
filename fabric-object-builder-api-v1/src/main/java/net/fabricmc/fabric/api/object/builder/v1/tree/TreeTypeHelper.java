/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.object.builder.v1.tree;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.mixin.object.builder.BlockStateProviderTypeInvoker;
import net.fabricmc.fabric.mixin.object.builder.FoliagePlacerTypeInvoker;
import net.fabricmc.fabric.mixin.object.builder.TreeDecoratorTypeInvoker;
import net.fabricmc.fabric.mixin.object.builder.TrunkPlacerTypeInvoker;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

/**
 * A registry for {@link TrunkPlacerType}s, {@link FoliagePlacerType}s, {@link TreeDecoratorType}s,
 * {@link BlockStateProviderType}s, and {@link IntProviderType}s.
 */
public final class TreeTypeHelper {
	/**
	 * Creates a new instance of a {@link TrunkPlacerType}, registers and returns it.<br>
	 * Uses {@link TrunkPlacerTypeInvoker} under the hood.
	 *
	 * <p>
	 *    Example:<br>
	 *
	 *    {@code
	 *          public static final TrunkPlacerType<MyTrunkPlacer> MY_TRUNK_PLACER =	FabricTreeRegistry.registerTrunkPlacerType(new Identifier("example", "my_trunk_placer"), MyTrunkPlacer.CODEC);
	 *    }
	 *
	 * @param id Registry {@link Identifier}
	 * @param codec The {@link Codec} associated with the {@link TrunkPlacer}
	 * @param <T> The owner {@link TrunkPlacer}
	 * @return Created and registered {@link TrunkPlacerType}
	 */
	public static <T extends TrunkPlacer> TrunkPlacerType<T> registerTrunkPlacerType(Identifier id, Codec<T> codec) {
		return TrunkPlacerTypeInvoker.invokeRegister(id.toString(), codec);
	}

	/**
	 * Creates a new instance of a {@link FoliagePlacerType}, registers and returns it.
	 *
	 * <p>
	 *     Example:<br>
	 *
	 *    {@code
	 *         public static final FoliagePlacerType<MyFoliagePlacer> MY_FOLIAGE_PLACER = FabricTreeRegistry.registerFoliagePlacerType(new Identifier("tutorial", "my_foliage_placer"), MyFoliagePlacer.CODEC);
	 *     }
	 * </p>
	 *
	 * @param id Registry {@link Identifier}
	 * @param codec The {@link Codec} associated with the {@link FoliagePlacer}
	 * @param <T> The owner {@link FoliagePlacer}
	 * @return Created and registered {@link FoliagePlacerType}
	 */
	public static <T extends FoliagePlacer> FoliagePlacerType<T> registerFoliagePlacerType(Identifier id, Codec<T> codec) {
		return FoliagePlacerTypeInvoker.invokeRegister(id.toString(), codec);
	}

	/**
	 * Creates a new instance of a {@link TreeDecoratorType}, registers and returns it.<br>
	 * Uses {@link TreeDecoratorTypeInvoker} under the hood.
	 *
	 * <p>
	 *     Example for {@link TreeDecoratorType}s:<br>
	 *
	 *     {@code
	 *         public static final TreeDecoratorType<MyTreeDecorator> MY_TREE_DECORATOR = FabricTreeRegistry.registerTreeDecoratorType(new Identifier("tutorial", "my_tree_decorator"), MyTreeDecorator.CODEC);
	 *     }
	 *
	 * @param id Registry {@link Identifier}
	 * @param codec The {@link Codec} associated with the {@link TreeDecorator}
	 * @param <T> The owner {@link TreeDecorator}
	 * @return Created and registered {@link TreeDecoratorType}
	 */
	public static <T extends TreeDecorator> TreeDecoratorType<T> registerTreeDecoratorType(Identifier id, Codec<T> codec) {
		return TreeDecoratorTypeInvoker.invokeRegister(id.toString(), codec);
	}

	/**
	 * Creates a new instance of a {@link BlockStateProviderType}, registers and returns it.
	 *
	 * <p>
	 *     Example for {@link BlockStateProviderType}s:<br>
	 *
	 *     {@code
	 *         public static final BlockStateProviderType<MyBlockStateProvider> MY_BLOCK_STATE_PROVIDER = FabricTreeRegistry.registerBlockStateProviderType(new Identifier("tutorial", "my_block_state_provider"), MyBlockStateProvider.CODEC);
	 *     }
	 *
	 * @param id Registry {@link Identifier}
	 * @param codec The {@link Codec} associated with the {@link BlockStateProvider}
	 * @param <T> The owner {@link BlockStateProvider}
	 * @return Created and registered {@link BlockStateProviderType}
	 */
	public static <T extends BlockStateProvider> BlockStateProviderType<T> registerBlockStateProviderType(Identifier id, Codec<T> codec) {
		return BlockStateProviderTypeInvoker.invokeRegister(id.toString(), codec);
	}

	/**
	 * A convenience method to avoid conflicts when using {@link IntProviderType#register}.
	 *
	 * <p>
	 *     Example for {@link IntProviderType}s:<br>
	 *
	 *     {@code
	 *         public static final IntProviderType<MyIntProvider> MY_INT_PROVIDER = FabricTreeRegistry.registerIntProviderType(new Identifier("tutorial", "my_int_provider"), MyIntProvider.CODEC);
	 *     }
	 *
	 * @param id Registry {@link Identifier}
	 * @param codec The {@link Codec} associated with the {@link IntProvider}
	 * @param <T> The owner {@link IntProvider}
	 * @return Created and registered {@link IntProviderType}
	 */
	public static <T extends IntProvider> IntProviderType<T> registerIntProviderType(Identifier id, Codec<T> codec) {
		return IntProviderType.register(id.toString(), codec);
	}
}
