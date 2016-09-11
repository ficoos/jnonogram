package org.bs.jnonogram.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;

public abstract class ObservableValueProxy<T, K> implements ObservableValue<T> {
    private final ObservableValue<K> observable;
    private final ObservableValue<T> proxy;
    private final HashMap<ChangeListener<T>, ChangeListener<K>> listenerHashMap = new HashMap<>();

    protected ObservableValue<K> getObservableValue() {
        return observable;
    }

    protected ObservableValueProxy(ObservableValue<K> observable) {
        this.observable = observable;
        proxy = this;
    }

    @Override
    public final void addListener(ChangeListener listener) {
        ChangeListener<K> wrappedListener = (observable, oldValue, newValue) -> listener.changed(proxy, convertValue(oldValue), convertValue(newValue));
        listenerHashMap.put(listener, wrappedListener);
        observable.addListener(wrappedListener);
    }

    @Override
    public final void removeListener(ChangeListener listener) {
        ChangeListener<K> wrappedListener = listenerHashMap.remove(listener);
        observable.removeListener(wrappedListener);
    }

    protected abstract T convertValue(K value);

    @Override
    public final T getValue() {
        return convertValue(observable.getValue());
    }

    @Override
    public final void addListener(InvalidationListener listener) {
        observable.addListener(listener);
    }

    @Override
    public final void removeListener(InvalidationListener listener) {
        observable.removeListener(listener);
    }
}
