package dev.webfx.kit.util.properties;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import dev.webfx.platform.util.collection.Collections;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Bruno Salmon
 */
public final class FXProperties {

    public static Unregisterable runOnPropertiesChange(Consumer<ObservableValue> consumer, ObservableValue... properties) {
        return new UnregisterableListener(consumer, properties);
    }

    public static Unregisterable runNowAndOnPropertiesChange(Consumer<ObservableValue> consumer, ObservableValue... properties) {
        consumer.accept(properties.length == 1 ? properties[0] : null);
        return runOnPropertiesChange(consumer, properties);
    }

    // Same API but with Collection instead of varargs

    public static Unregisterable runNowAndOnPropertiesChange(Consumer<ObservableValue> consumer, Collection<ObservableValue> properties) {
        return runNowAndOnPropertiesChange(consumer, Collections.toArray(properties, ObservableValue[]::new));
    }

    public static Unregisterable runOnPropertiesChange(Consumer<ObservableValue> consumer, Collection<ObservableValue> properties) {
        return runOnPropertiesChange(consumer, Collections.toArray(properties, ObservableValue[]::new));
    }

    // Same API but with Runnable instead of Consumer

    public static Unregisterable runOnPropertiesChange(Runnable runnable, ObservableValue... properties) {
        return runOnPropertiesChange(p -> runnable.run(), properties);
    }

    public static Unregisterable runNowAndOnPropertiesChange(Runnable runnable, ObservableValue... properties) {
        return runNowAndOnPropertiesChange(p -> runnable.run(), properties);
    }

    public static Unregisterable runNowAndOnPropertiesChange(Runnable runnable, Collection<ObservableValue> properties) {
        return runNowAndOnPropertiesChange(p -> runnable.run(), properties);
    }

    public static Unregisterable runOnPropertiesChange(Runnable runnable, Collection<ObservableValue> properties) {
        return runOnPropertiesChange(p -> runnable.run(), properties);
    }


    public static <T, R> ObservableValue<R> compute(ObservableValue<? extends T> p, Function<? super T, ? extends R> function) {
        Property<R> combinedProperty = new SimpleObjectProperty<>();
        runNowAndOnPropertiesChange(arg -> combinedProperty.setValue(function.apply(p.getValue())), p);
        return combinedProperty;
    }

    public static <T1, T2, R> ObservableValue<R> combine(ObservableValue<? extends T1> p1, ObservableValue<? extends T2> p2, BiFunction<? super T1, ? super T2, ? extends R> combineFunction) {
        Property<R> combinedProperty = new SimpleObjectProperty<>();
        runNowAndOnPropertiesChange(arg -> combinedProperty.setValue(combineFunction.apply(p1.getValue(), p2.getValue())), p1, p2);
        return combinedProperty;
    }

    public static <T> ObservableValue<T> filter(ObservableValue<T> property, Predicate<T> predicate) {
        Property<T> filteredProperty = new SimpleObjectProperty<>();
        runNowAndOnPropertiesChange(arg -> { if (predicate.test(property.getValue())) filteredProperty.setValue(property.getValue()); }, property);
        return filteredProperty;
    }

    public static <T> void consume(ObservableValue<T> property, Consumer<T> consumer) {
        runNowAndOnPropertiesChange(p -> consumer.accept(property.getValue()), property);
    }

    public static <T> void setIfNotBound(Property<T> property, T value) {
        if (!property.isBound())
            property.setValue(value);
    }

    public static <T> void setEvenIfBound(Property<T> property, T value) {
        if (property.isBound())
            property.unbind();
        property.setValue(value);
    }

    public static <T> void setIfNotEquals(Property<T> property, T value) {
        if (!Objects.equals(value, property.getValue()))
            property.setValue(value);
    }

    public static <T> void onPropertySet(ObservableValue<T> property, Consumer<T> valueConsumer) {
        onPropertySet(property, valueConsumer, false);
    }

    public static <T> void onPropertySet(ObservableValue<T> property, Consumer<T> valueConsumer, boolean callIfNullProperty) {
        if (property == null) {
            if (callIfNullProperty)
                valueConsumer.accept(null);
        } else {
            T value = property.getValue();
            if (value != null)
                valueConsumer.accept(value);
            else
                property.addListener(new ChangeListener<T>() {
                    @Override
                    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                        if (newValue != null) {
                            observable.removeListener(this);
                            valueConsumer.accept(newValue);
                        }
                    }
                });
        }
    }

    public static <A, B> void bindConverted(Property<A> pA, ObservableValue<B> pB, Function<B, A> baConverter) {
        pA.bind(compute(pB, baConverter));
    }

    public static <A, B> void bindConvertedBidirectional(Property<A> pA, Property<B> pB, Function<B, A> baConverter, Function<A, B> abConverter) {
        boolean[] converting = { false };
        pB.addListener(new ChangeListener<B>() {
            @Override
            public void changed(ObservableValue<? extends B> observable, B oldValue, B newValue) {
                if (!converting[0]) {
                    converting[0] = true;
                    try {
                        pA.setValue(baConverter.apply(newValue));
                    } finally {
                        converting[0] = false;
                    }
                }
            }
        });
        pA.addListener(new ChangeListener<A>() {
            @Override
            public void changed(ObservableValue<? extends A> observable, A oldValue, A newValue) {
                if (!converting[0]) {
                    converting[0] = true;
                    try {
                        pB.setValue(abConverter.apply(newValue));
                    } finally {
                        converting[0] = false;
                    }
                }
            }
        });
    }

}