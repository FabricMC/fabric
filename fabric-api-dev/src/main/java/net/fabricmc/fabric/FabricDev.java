package net.fabricmc.fabric;

public class FabricDev {
	public static final boolean ZERO_WEIGHT_WARNING = getProperty("zeroWeightWarning", true);
	public static final boolean LOG_MISSING_TRANSLATIONS = getProperty("logMissingTranslations", true);
	public static final boolean LOG_CONVENTION_ISSUES = getProperty("logConventionIssues", true);
	public static final boolean ALWAYS_AGREE_TO_EULA = getProperty("alwaysAgreeToEula", false);
	public static final boolean TICK_TEST_MANAGER = getProperty("tickTestManager", false);
	public static final boolean REGISTER_DEBUG_COMMANDS = getProperty("registerDebugCommands", true);
	public static final boolean REGISTER_TEST_ARGUMENTS = getProperty("registerTestArguments", false);
	public static final boolean ENABLE_COMMAND_EXCEPTION_LOGGING = getProperty("enableCommandExceptionLogging", true);
	public static final boolean ENABLE_COMMAND_ARGUMENT_LOGGING = getProperty("enableCommandArgumentLogging", true);
	public static final boolean THROW_ON_INVALID_BLOCK_BOXES = getProperty("throwOnInvalidBlockBoxes", true);
	public static final boolean ENABLE_UNPRIMED_HEIGHTMAP_LOGGING = getProperty("enableUnprimedHeightmapLogging", true);
	public static final boolean ENABLE_LOADING_STRUCTURES_FROM_GAMETESTS = getProperty("enableLoadingStructuresFromGameTests", true);
	public static final boolean THROW_ON_MISSING_DATA_FIXERS = getProperty("throwOnMissingDataFixers", false);
	public static final boolean ENABLE_SUPPLIER_AND_RUNNABLE_DEBUGGING = getProperty("enableSupplierAndRunnableDebugging", false);
	public static final boolean ENABLE_EXCEPTION_IDE_PAUSING = getProperty("enableExceptionIdePausing", true);

	private static boolean getProperty(String name, boolean defaultValue) {
		try {
			return "true".equalsIgnoreCase(System.getProperty(name));
		} catch (Throwable e) {
			return defaultValue;
		}
	}
}
