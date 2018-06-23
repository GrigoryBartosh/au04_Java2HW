package ru.spbau.gbarto;

import java.util.function.Function;

/**
 * Stores tasks and provides interaction with thread pool.
 *
 * @param <T> type of result that task returns
 */
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
    <U> LightFuture<U> thenApply(Function<T, U> function);
}
