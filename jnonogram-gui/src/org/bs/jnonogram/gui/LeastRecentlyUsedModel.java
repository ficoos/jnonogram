package org.bs.jnonogram.gui;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;

public class LeastRecentlyUsedModel<T> {
    private final ListProperty<T> _itemsProperty;
    private final long maxItems;

    public LeastRecentlyUsedModel(long maxItems, Collection<T> initialList) {
        this.maxItems = maxItems;
        _itemsProperty = new SimpleListProperty<>(this, "Items", FXCollections.observableArrayList(initialList));

    }
    public LeastRecentlyUsedModel(long maxItems) {
        this(maxItems, FXCollections.observableArrayList());
    }

    public ReadOnlyListProperty<T> itemsProperty() {
        return this._itemsProperty;
    }

    public void addItem(T item) {
        if (_itemsProperty.contains(item)) {
            _itemsProperty.remove(item);
        }

        _itemsProperty.add(0, item);
        while (_itemsProperty.size() > maxItems) {
            _itemsProperty.remove(_itemsProperty.size() - 1);
        }
    }

    public void clear() {
        _itemsProperty.clear();
    }

    public ObservableList<T> items() {
        return _itemsProperty.getValue();
    }
}
