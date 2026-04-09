package dev.ari.polarcli;

final class CliException extends RuntimeException {
    CliException(String message) {
        super(message);
    }
}
