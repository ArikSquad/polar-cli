package dev.ari.polarcli;

import net.hollowcube.polar.PolarWorld;

import java.nio.file.Path;

record CliOptions(
        Path input,
        Path output,
        Integer radius,
        int centerX,
        int centerZ,
        int minSection,
        int maxSection,
        PolarWorld.CompressionType compression,
        boolean overwrite,
        boolean help
) {
}
