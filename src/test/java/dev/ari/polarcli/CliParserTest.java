package dev.ari.polarcli;

import net.hollowcube.polar.PolarWorld;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliParserTest {
    @Test
    void parsesRequiredPositionalInputAndDefaultOutput() {
        CliOptions options = CliParser.parse(new String[]{"world"});

        assertEquals(Path.of("world"), options.input());
        assertEquals(Path.of("world.polar"), options.output());
        assertEquals(-4, options.minSection());
        assertEquals(19, options.maxSection());
        assertEquals(PolarWorld.CompressionType.ZSTD, options.compression());
    }

    @Test
    void parsesOptionalFlags() {
        CliOptions options = CliParser.parse(new String[]{
                "--input", "world",
                "--output", "build/out/custom.polar",
                "--radius", "8",
                "--center-x", "12",
                "--center-z", "-4",
                "--min-section", "-6",
                "--max-section", "24",
                "--compression", "none",
                "--overwrite"
        });

        assertEquals(Path.of("world"), options.input());
        assertEquals(Path.of("build/out/custom.polar"), options.output());
        assertEquals(8, options.radius());
        assertEquals(12, options.centerX());
        assertEquals(-4, options.centerZ());
        assertEquals(-6, options.minSection());
        assertEquals(24, options.maxSection());
        assertEquals(PolarWorld.CompressionType.NONE, options.compression());
    }

    @Test
    void rejectsCenterWithoutRadius() {
        CliException exception = assertThrows(CliException.class, () -> CliParser.parse(new String[]{
                "world",
                "--center-x", "1"
        }));

        assertEquals("--center-x and --center-z require --radius.", exception.getMessage());
    }

    @Test
    void rejectsInvalidSectionRange() {
        CliException exception = assertThrows(CliException.class, () -> CliParser.parse(new String[]{
                "world",
                "--min-section", "4",
                "--max-section", "3"
        }));

        assertEquals("--max-section must be greater than or equal to --min-section.", exception.getMessage());
    }
}
