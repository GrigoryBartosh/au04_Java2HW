package ru.spbau.gbarto.exceptions;

public class TooManyAnnotationsException extends Exception {
    public TooManyAnnotationsException(String annotation) {
        super("Too many exceptions: " + annotation);
    }
}
