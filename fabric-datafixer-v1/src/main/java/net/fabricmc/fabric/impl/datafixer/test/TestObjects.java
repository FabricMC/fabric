package net.fabricmc.fabric.impl.datafixer.test;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.fabricmc.fabric.api.datafixer.v1.FabricSchemas;
import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;
import net.minecraft.util.SystemUtil;

public class TestObjects {

	private static final int VERSION = 1;

	public static void create() {
		DataFixerBuilder builder = new DataFixerBuilder(VERSION);
		builder.addSchema(0, FabricSchemas.FABRIC_SCHEMA);
		Schema schema_1 = builder.addSchema(1, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);
		SimpleFixes.INSTANCE.addBlockRenameFix(builder, "rename test", "test:oldblock", "test:newblock", schema_1);

		DataFixerHelper.INSTANCE.registerFixer("fabric:datafixer", VERSION, builder.build(SystemUtil.getServerWorkerExecutor()));
	}
}
