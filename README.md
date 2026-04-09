# polar-cli

Java 25 command-line tool for converting Minecraft Anvil worlds into the HollowCube Polar format.

## Build

```bash
./gradlew installDist
```

## Run

```bash
build/install/polar-cli/bin/polar-cli <world-dir> [output.polar]
```

Example:

```bash
build/install/polar-cli/bin/polar-cli ./world ./world.polar --radius 8 --center-x 0 --center-z 0
```

## Options

```text
-i, --input <path>          Path to the Anvil world directory.
-o, --output <path>         Output .polar file. Defaults to <world-dir>.polar.
-f, --overwrite             Replace the output file if it already exists.
    --radius <chunks>       Convert only a chunk radius around the center.
    --center-x <chunk-x>    Center chunk X for --radius. Defaults to 0.
    --center-z <chunk-z>    Center chunk Z for --radius. Defaults to 0.
    --min-section <value>   Minimum section to export. Defaults to -4.
    --max-section <value>   Maximum section to export. Defaults to 19.
    --compression <type>    Compression: zstd or none. Defaults to zstd.
-h, --help                  Show help.
```
