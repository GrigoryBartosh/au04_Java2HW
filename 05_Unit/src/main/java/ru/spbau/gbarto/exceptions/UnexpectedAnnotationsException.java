package ru.spbau.gbarto.exceptions;

public class UnexpectedAnnotationsException extends Exception {
    public UnexpectedAnnotationsException() {
        super("Unexpected annotations");
    }
}