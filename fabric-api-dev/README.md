`Weight#validate`:
- Argument: `fabric.dev.zeroWeightWarning`
- Default: true
- Logs an error when a weight is set to 0

`Bootstrap#logMissing`:
- Argument `fabric.dev.logMissingTranslations`
- Default: true
- Logs an error when a translation is missing

`CommandManager#checkMissing`:
- Argument `fabric.dev.enableCommandArgumentLogging`
- Default: true
- Logs an error regarding argument ambiguity and throws an exception if an argument type is not registered

`Block#<init>` & `Item#<init>`:
- Argument `fabric.dev.logConventionIssues`
- Default: true
- Logs an error if Block classes don't end with Block and if Item classes don't end with Item

`EulaReader#<init>` & `EulaReader#createEulaFile`:
- Argument: `fabric.dev.alwaysAgreeToEula`
- Default: false
- Note: By enabling this, you declare that you have agreed to the EULA.
- Skips creating the eula.txt file and always agrees to the EULA

`CommandManager#<init>`:
- Argument: `fabric.dev.registerDebugCommands`
- Default: true
- Register's Minecraft's debug commands (TestCommand, RaidCommand, DebugPathCommand, DebugMobSpawningCommand, WardenSpawnTrackerCommand, SpawnArmorTrimsCommand, ServerPackCommand) and if on the server DebugConfigCommand

`CommandManager#execute`:
- Argument: `fabric.dev.enableCommandExceptionLogging`
- Default: true
- Logs an error if a command threw an exception

`BlockBox#<init>`
- Argument: `fabric.dev.throwOnInvalidBlockBoxes`
- Default: true
- Throw's an exception if a bounding box is invalid

`Chunk#sampleHeightmap`:
- Argument: `fabric.dev.enableUnprimedHeightmapLogging`
- Default: true
- Logs an error if the heightmap is null

`Util#debugRunnable` & `Util#debugSupplier`:
- Argument: `fabric.dev.enableSupplierAndRunnableDebugging`
- Default: false
- Set's the current thread's name to the activeThreadName if debugRunnable or debugSupplier is called

`Util#error` & `Util#throwOrPause`:
- Argument: `fabric.dev.enableExceptionIdePausing`
- Default: true
- Call's a method in which you should have a breakpoint to debug errors thrown with Util#error and exceptions thrown with Util#throwOrPause
