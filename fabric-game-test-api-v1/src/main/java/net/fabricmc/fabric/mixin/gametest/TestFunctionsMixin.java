package net.fabricmc.fabric.mixin.gametest;

import net.fabricmc.fabric.impl.client.gametest.FabricGameTestHelperImpl;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(TestFunctions.class)
public abstract class TestFunctionsMixin {

	@Inject(at = @At("HEAD"), method = "getTestFunction(Ljava/lang/reflect/Method;)Lnet/minecraft/test/TestFunction;", cancellable = true)
	private static void getTestFunction(Method method, CallbackInfoReturnable<TestFunction> cir) {
		GameTest gameTest = method.getAnnotation(GameTest.class);
		String testSuiteName = method.getDeclaringClass().getSimpleName().toLowerCase();
		String testCaseName = testSuiteName + "." + method.getName().toLowerCase();

		String modId = FabricGameTestHelperImpl.getModIdForTestClass(method.getDeclaringClass());

		String structureName = "%s:%s".formatted(modId, testCaseName);
		if (!gameTest.structureName().isEmpty()) {
			structureName = "%s:%s".formatted(modId, gameTest.structureName());
		}

		TestFunction testFunction = new TestFunction(gameTest.batchId(),
				testCaseName,
				structureName,
				StructureTestUtil.getRotation(gameTest.rotation()),
				gameTest.tickLimit(),
				gameTest.duration(),
				gameTest.required(),
				gameTest.requiredSuccesses(),
				gameTest.maxAttempts(),
				FabricGameTestHelperImpl.invokeTestMethod(method)
		);

		cir.setReturnValue(testFunction);
	}
}
