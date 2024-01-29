package net.fabricmc.fabric.test.transfer.gametests;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;

import net.minecraft.fluid.Fluids;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class CodecsTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testDecode(TestContext context) {
		String input = """
				{
					"variant": {
						"fluid": "minecraft:water",
						"nbt": {
							"test": 42
						}
					},
					"amount": 81000
				}
				""";

		JsonElement json = JsonParser.parseString(input);

		DataResult<Pair<ResourceAmount<FluidVariant>, JsonElement>> result = FluidVariant.AMOUNT_CODEC.decode(JsonOps.INSTANCE, json);
		context.assertTrue(result.result().isPresent(), "Couldn't decode JSON");

		ResourceAmount<FluidVariant> decoded = result.result().get().getFirst();
		context.assertTrue(decoded.resource().getFluid() == Fluids.WATER, "Fluid was not water");
		context.assertTrue(decoded.resource().getNbt() != null, "NBT was null");
		context.assertTrue(!decoded.resource().getNbt().isEmpty(), "NBT was empty");
		context.assertTrue(decoded.amount() == 81000, "Amount was not 81000");

		context.complete();
	}
}
