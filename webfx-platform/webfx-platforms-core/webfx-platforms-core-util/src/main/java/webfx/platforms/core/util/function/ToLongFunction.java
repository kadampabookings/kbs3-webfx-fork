package webfx.platforms.core.util.function;

/**
 * @author Bruno Salmon
 */
public interface ToLongFunction<T> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    long applyAsLong(T value);
}