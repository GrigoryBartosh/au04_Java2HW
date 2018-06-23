package ru.spbau.gbarto;

/**
 * An exception that throws if the corresponding Supplier task is completed with the exception.
 */
public class LightExecutionException extends Exception{
    LightExecutionException(Exception e) {
        super(e);
    }
}
