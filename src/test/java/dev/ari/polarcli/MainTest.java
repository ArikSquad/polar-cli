package dev.ari.polarcli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    void helpExitsSuccessfully() {
        assertEquals(0, Main.run(new String[]{"--help"}));
    }

    @Test
    void missingInputReturnsUsageError() {
        assertEquals(2, Main.run(new String[0]));
    }

    @Test
    void convertsEmptyWorld(@TempDir Path tempDir) throws Exception {
        Path world = Files.createDirectories(tempDir.resolve("world").resolve("region")).getParent();
        Path output = tempDir.resolve("world.polar");

        int exitCode = Main.run(new String[]{world.toString(), output.toString()});

        assertEquals(0, exitCode);
        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
    }
}
