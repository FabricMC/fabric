package net.fabricmc.fabric.impl.datafixer.mixin;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.datafixers.schemas.Schema99;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema99.class)
public class MixinSchema99 {
	private static final Logger LOGGER = LogManager.getLogger("Fabric-DataFixer");

	static {
		LOGGER.info("[Fabric-DataFixer] Initializing DataFixer types.");
	}

	@Inject(at = @At("TAIL"), method = "registerEntities(Lcom/mojang/datafixers/schemas/Schema;)Ljava/util/Map;", cancellable = true)
	public void registerEntitiesHelper(Schema schema_1, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
		List<DataFixerEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("fabric:datafixer", DataFixerEntrypoint.class);

		final Map<String, Supplier<TypeTemplate>> entityMap = cir.getReturnValue();
		for(DataFixerEntrypoint entry : entrypoints) {
			entry.registerEntities(schema_1, entityMap);
		}
		cir.setReturnValue(entityMap);
	}

	@Inject(at = @At("TAIL"), method = "registerBlockEntities(Lcom/mojang/datafixers/schemas/Schema;)Ljava/util/Map;", cancellable = true)
	public void registerBlockEntitiesHelper(Schema schema_1, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
		List<DataFixerEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("fabric:datafixer", DataFixerEntrypoint.class);

		final Map<String, Supplier<TypeTemplate>> blockEntityMap = cir.getReturnValue();
		for(DataFixerEntrypoint entry : entrypoints) {
			entry.registerBlockEntities(schema_1, blockEntityMap);
		}
		cir.setReturnValue(blockEntityMap);
	}

	@Inject(at = @At("TAIL"), method = "registerTypes(Lcom/mojang/datafixers/schemas/Schema;Ljava/util/Map;Ljava/util/Map;)V")
	public void registerTypesHelper(Schema schema_1, Map<String, Supplier<TypeTemplate>> entityMap, Map<String, Supplier<TypeTemplate>> blockEntityMap, CallbackInfo ci) {
		List<DataFixerEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("fabric:datafixer", DataFixerEntrypoint.class);

		for(DataFixerEntrypoint entry : entrypoints) {
			entry.registerTypes(schema_1, entityMap, blockEntityMap);
		}
	}
}
