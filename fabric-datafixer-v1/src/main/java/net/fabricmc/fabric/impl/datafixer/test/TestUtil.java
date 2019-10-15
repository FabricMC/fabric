package net.fabricmc.fabric.impl.datafixer.test;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TestUtil {

	public static int TEST_DATA_VERSION = 1;
	public static final Block TEST_B = Registry.register(Registry.BLOCK, new Identifier("test", "testblock"), new Block(FabricBlockSettings.of(Material.EARTH).build()));
	private static final String MODID = "";
	private static final int MOD_DATAFIXER_VERSION = 1;
	//public static final BlockEntityType<TestBE> TEST = BlockEntityType.Builder.create(TestBE::new, TEST_B).build(getChoiceType(DataFixerHelper.INSTANCE.getDataFixer(MODID), MOD_DATAFIXER_VERSION, TypeReferences.BLOCK_ENTITY, "test:testblockentity"));

	public static void create() {

	}

	public static class TestBE/* extends BlockEntity*/ {

		public TestBE() {
			//super(TEST);
		}
	}

	public static Type<?> getChoiceType(DataFixer dataFixer, int schemaVersion, DSL.TypeReference typeReference, String identifier) {
		Schema schema = dataFixer.getSchema(DataFixUtils.makeKey(schemaVersion));

		if(schema == null){
			throw new IllegalArgumentException("DataFixer does not contain a Schema with a version of " + schemaVersion);
		}

		return schema.getChoiceType(typeReference, identifier);
	}
}
