package ru.spbau.gbarto.exceptions;

public class ExceptionNotThrown extends Exception {
    public ExceptionNotThrown(Class expected) {
        super("Expected " + expected.getName() + " was not thrown");
    }
}
