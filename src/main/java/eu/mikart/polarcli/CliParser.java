package eu.mikart.polarcli;

import net.hollowcube.polar.PolarWorld;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class CliParser {
    private CliParser() {
    }

    static CliOptions parse(String[] args) {
        Path input = null;
        Path output = null;
        Integer radius = null;
        Integer centerX = null;
        Integer centerZ = null;
        int minSection = -4;
        int maxSection = 19;
        PolarWorld.CompressionType compression = PolarWorld.DEFAULT_COMPRESSION;
        boolean overwrite = false;
        boolean help = false;
        List<String> positionals = new ArrayList<>();

        for (int index = 0; index < args.length; index++) {
            String arg = args[index];
            switch (arg) {
                case "-h", "--help" -> help = true;
                case "-f", "--overwrite" -> overwrite = true;
                case "-i", "--input" -> input = Path.of(requireValue(args, ++index, arg));
                case "-o", "--output" -> output = Path.of(requireValue(args, ++index, arg));
                case "--radius" -> radius = parseInt(requireValue(args, ++index, arg), arg);
                case "--center-x" -> centerX = parseInt(requireValue(args, ++index, arg), arg);
                case "--center-z" -> centerZ = parseInt(requireValue(args, ++index, arg), arg);
                case "--min-section" -> minSection = parseInt(requireValue(args, ++index, arg), arg);
                case "--max-section" -> maxSection = parseInt(requireValue(args, ++index, arg), arg);
                case "--compression" -> compression = parseCompression(requireValue(args, ++index, arg));
                default -> {
                    if (arg.startsWith("-")) {
                        throw new CliException("Unknown option: " + arg);
                    }
                    positionals.add(arg);
                }
            }
        }

        if (help) {
            return new CliOptions(input, output, radius, defaulted(centerX), defaulted(centerZ), minSection, maxSection, compression, overwrite, true);
        }

        if (input == null && !positionals.isEmpty()) {
            input = Path.of(positionals.removeFirst());
        }
        if (output == null && !positionals.isEmpty()) {
            output = Path.of(positionals.removeFirst());
        }
        if (!positionals.isEmpty()) {
            throw new CliException("Too many positional arguments.");
        }
        if (input == null) {
            throw new CliException("Missing input world path.");
        }
        if ((centerX != null || centerZ != null) && radius == null) {
            throw new CliException("--center-x and --center-z require --radius.");
        }
        if (radius != null && radius < 0) {
            throw new CliException("--radius must be zero or greater.");
        }
        if (maxSection < minSection) {
            throw new CliException("--max-section must be greater than or equal to --min-section.");
        }

        int resolvedCenterX = defaulted(centerX);
        int resolvedCenterZ = defaulted(centerZ);
        Path resolvedOutput = output != null ? output : defaultOutput(input);

        return new CliOptions(input, resolvedOutput, radius, resolvedCenterX, resolvedCenterZ, minSection, maxSection, compression, overwrite, false);
    }

    static String usage() {
        return """
                Usage:
                  polar-cli --input <world-dir> [--output <file.polar>] [options]
                  polar-cli <world-dir> [file.polar] [options]

                Options:
                  -i, --input <path>          Path to the Anvil world directory.
                  -o, --output <path>         Output .polar file. Defaults to <world-dir>.polar.
                  -f, --overwrite             Replace the output file if it already exists.
                      --radius <chunks>       Convert only a chunk radius around the center.
                      --center-x <chunk-x>    Center chunk X for --radius. Defaults to 0.
                      --center-z <chunk-z>    Center chunk Z for --radius. Defaults to 0.
                      --min-section <value>   Minimum section to export. Defaults to -4.
                      --max-section <value>   Maximum section to export. Defaults to 19.
                      --compression <type>    Compression: zstd or none. Defaults to zstd.
                  -h, --help                  Show this help text.
                """;
    }

    private static String requireValue(String[] args, int index, String option) {
        if (index >= args.length) {
            throw new CliException("Missing value for " + option + ".");
        }
        return args[index];
    }

    private static int parseInt(String value, String option) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new CliException("Invalid integer for " + option + ": " + value);
        }
    }

    private static PolarWorld.CompressionType parseCompression(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "zstd" -> PolarWorld.CompressionType.ZSTD;
            case "none" -> PolarWorld.CompressionType.NONE;
            default -> throw new CliException("Invalid compression type: " + value + ". Expected zstd or none.");
        };
    }

    private static int defaulted(Integer value) {
        return value != null ? value : 0;
    }

    private static Path defaultOutput(Path input) {
        Path normalized = input.normalize();
        Path parent = normalized.getParent();
        String name = normalized.getFileName() != null ? normalized.getFileName().toString() : "world";
        Path fileName = Path.of(name + ".polar");
        return parent != null ? parent.resolve(fileName) : fileName;
    }
}
