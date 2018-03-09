package ru.spbau.gbarto;

import java.util.function.Supplier;

/**
 * Factory of Lazy objects.
 *
 * @param <T> type of return Lazy object
 */
public class LazyFactory<T> {
    /**
     * Creates of new Lazy objects.
     *
     * @param supplier given Supplier
     * @param <T> type of return Lazy object
     * @return Lazy object
     */
    public static <T> Lazy<T> createLazy(Supplier<T> supplier) {
        class LazyConstructor<K> implements Lazy<K> {

            private Supplier<K> localSupplier;
            private K result = null;

            private LazyConstructor(Supplier<K> supplier) {
                localSupplier = supplier;
            }

            @Override
            public K get() {
                if (localSupplier != null) {
                    result = localSupplier.get();
                    localSupplier = null;
                }
                return result;
            }
        }

        return new LazyConstructor<>(supplier);
    }

    /**
     * Creates of new thread safe Lazy objects.
     *
     * @param supplier given Supplier
     * @param <T> type of return Lazy object
     * @return Lazy object
     */
    public static <T> Lazy<T> createThreadLazy(Supplier<T> supplier) {
        class LazyConstructor<K> implements Lazy<K> {

            private volatile Supplier<K> localSupplier;
            private K result = null;

            private LazyConstructor(Supplier<K> supplier) {
                localSupplier = supplier;
            }

            @Override
            public K get() {
                if (localSupplier != null) {
                    synchronized (this) {
                        if (localSupplier != null) {
                            result = localSupplier.get();
                            localSupplier = null;
                        }
                    }
                }
                return result;
            }
        }

        return new LazyConstructor<>(supplier);
    }
}
