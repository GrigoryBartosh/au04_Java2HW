package ru.spbau.gbarto;

import java.util.function.Function;

public interface LightFuture<T> {

    /**
     * Returns true if the task is completed.
     *
     * @return true if the task is completed
     */
    boolean isReady();

    /**
     * Returns the result of the task execution.
     *
     * @return the result of the task execution
     * @throws LightExecutionException if the corresponding Supplier task is completed with the exception
     */
    T get() throws LightExecutionException;

    /**
     * Takes an object of type Function, which can be applied to the result of this task X and returns a new task Y, accepted for execution.
     *
     * @param function for applying
     * @return the result of the task execution
     */
    LightFuture<T> thenApply(Function<T, T> function);

    /**
     * An exception that throws if the corresponding Supplier task is completed with the exception.
     */
    class LightExecutionException extends Exception {
        LightExecutionException(Exception e) {
            super(e);
        }
    }
}
