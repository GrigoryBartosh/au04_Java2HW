package ru.spbau.gbarto;

/**
 * Describes the interface that does a deferred calculation.
 *
 * @param <T> type of return value
 */
public interface Lazy<T> {
    /**
     * The first call to get calls the calculation and returns the result.
     * Repeated calls get return the same object as the first call.
     * Calculation should be started no more than once.
     *
     * @return the result of calculation
     */
    T get();
}
