package naga.toolkit.util;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import naga.commons.util.Arrays;
import naga.commons.util.collection.Collections;
import naga.commons.util.function.Converter;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public class ObservableLists {

    public static <T> void setAllNonNulls(ObservableList<T> list, T... elements) {
        if (elements != null) {
            if (Arrays.allNonNulls(elements))
                list.setAll(elements);
            list.setAll(Arrays.nonNullsAsList(elements));
        }
    }

    public static <A, B> void setAllConverted(List<A> aList, Converter<A, B> aToBConverter, ObservableList<B> bList) {
        // GWT/J2OBJC bList.setAll(aList.stream().map(aToBConverter::convert).collect(Collectors.toList()));
        bList.setAll(Collections.convert(aList, aToBConverter));
    }

    public static <T> void bind(ObservableList<T> list1, ObservableList<T> list2) {
        runNowAndOnListChange(() -> list1.setAll(list2), list2);
    }

    public static <A, B> void bindConverted(ObservableList<A> aList, ObservableList<B> bList, Converter<B, A> bToAConverter) {
        runNowAndOnListChange(() -> setAllConverted(bList, bToAConverter, aList), bList);
    }

    public static void runNowAndOnListChange(Runnable runnable, ObservableList list) {
        runnable.run();
        runOnListChange(runnable, list);
    }

    public static void runOnListChange(Runnable runnable, ObservableList list) {
        list.addListener((ListChangeListener) c -> runnable.run());
    }
}