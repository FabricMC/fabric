package net.fabricmc.fabric.mixin.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.resource.Resource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Mixin(StructureTestUtil.class)
public abstract class StructureTestUtilMixin {
	private static final String GAMETEST_STRUCTURE_PATH = "gametest/structures/";

	// Replace the default test structure loading with something that works a bit better for mods.
	@Inject(at = @At("HEAD"), method = "createStructure(Ljava/lang/String;Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/structure/Structure;", cancellable = true)
	private static void createStructure(String id, ServerWorld world, CallbackInfoReturnable<Structure> cir) {
		Identifier baseId = new Identifier(id);
		Identifier structureId = new Identifier(baseId.getNamespace(), GAMETEST_STRUCTURE_PATH + baseId.getPath() + ".snbt");

		try {
			Resource resource = world.getServer().getResourceManager().getResource(structureId);
			String snbt;

			try (InputStream inputStream = resource.getInputStream()) {
				snbt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}

			NbtCompound nbtCompound = NbtHelper.method_32260(snbt);
			Structure structure = world.getStructureManager().createStructure(nbtCompound);

			cir.setReturnValue(structure);
		} catch (IOException | CommandSyntaxException e) {
			throw new RuntimeException("Error while trying to load structure: " + structureId, e);
		}
	}
}
