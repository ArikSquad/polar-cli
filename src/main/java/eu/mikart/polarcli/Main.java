package eu.mikart.polarcli;

import net.minestom.server.MinecraftServer;
import net.hollowcube.polar.AnvilPolar;
import net.hollowcube.polar.ChunkSelector;
import net.hollowcube.polar.PolarWorld;
import net.hollowcube.polar.PolarWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Main {
    private static volatile boolean minestomInitialized;

    private Main() {
    }

    static void main(String[] args) {
        System.exit(run(args));
    }

    static int run(String[] args) {
        try {
            CliOptions options = CliParser.parse(args);
            if (options.help()) {
                System.out.print(CliParser.usage());
                return 0;
            }

            validateInput(options.input());
            validateOutput(options.output(), options.overwrite());

            ChunkSelector selector = options.radius() == null
                    ? ChunkSelector.all()
                    : ChunkSelector.radius(options.centerX(), options.centerZ(), options.radius());

            ensureMinestomInitialized();
            PolarWorld world = AnvilPolar.anvilToPolar(
                    options.input(),
                    options.minSection(),
                    options.maxSection(),
                    selector
            );
            world.setCompression(options.compression());

            byte[] data = PolarWriter.write(world);
            writeOutput(options.output(), data, options.overwrite());

            System.out.printf(
                    "Converted %d chunks from %s to %s (%s, %d bytes).%n",
                    world.chunks().size(),
                    options.input().toAbsolutePath(),
                    options.output().toAbsolutePath(),
                    options.compression().name().toLowerCase(),
                    data.length
            );
            return 0;
        } catch (CliException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.err.println();
            System.err.print(CliParser.usage());
            return 2;
        } catch (IOException exception) {
            System.err.println("Conversion failed: " + exception.getMessage());
            return 1;
        } catch (RuntimeException exception) {
            System.err.println("Unexpected failure: " + exception.getMessage());
            return 1;
        }
    }

    private static void ensureMinestomInitialized() {
        if (minestomInitialized) {
            return;
        }
        synchronized (Main.class) {
            if (minestomInitialized) {
                return;
            }
            MinecraftServer.init();
            minestomInitialized = true;
        }
    }

    private static void validateInput(Path input) {
        if (!Files.isDirectory(input)) {
            throw new CliException("Input path is not a directory: " + input);
        }
        Path regionDirectory = input.resolve("region");
        if (!Files.isDirectory(regionDirectory)) {
            throw new CliException("Input world is missing a region directory: " + regionDirectory);
        }
    }

    private static void validateOutput(Path output, boolean overwrite) {
        if (Files.exists(output) && !overwrite) {
            throw new CliException("Output file already exists. Pass --overwrite to replace it: " + output);
        }
    }

    private static void writeOutput(Path output, byte[] data, boolean overwrite) throws IOException {
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (overwrite) {
            Files.write(output, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return;
        }

        Files.write(output, data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }
}
