# Fabric Data Fixer API (v1)
For user-facing documentation, please check the Javadoc.

This is a port of data fixer API from QSL, with some internal changes and new methods:

- Unlike QSL, this API saves the data version in a NBT compound `_FabricDataVersions`.
- Current data version can be specified in the `fabric.mod.json` file.
- `FabricDataFixerBuilder#build()` overload that uses bootstrap executor.

Files with ported code are marked with `// From QSL.` comment and a special license header.

## Running the tests
The repository contains three files: `level.dat` and two region files (with chunks trimmed). You can run `gradlew runTestmodServer` to test the updating. If this does not error, the tests passed!

To generate the file (without running the tests), run `gradlew runGenOldSave`, then optionally trim the chunks with NBT editing tools.

The test files contain:

- A chest block with a modded item, at `0, 10, 0` in the overworld
- A modded chest block with a modded item, at `0, 9, 0` in the overworld
- A modded block at `0, 8, 0` in the overworld
- A modded biome at chunk `0, 0` in the End
