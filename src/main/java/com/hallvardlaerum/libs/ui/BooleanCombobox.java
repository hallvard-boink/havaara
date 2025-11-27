package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.combobox.ComboBox;

public class BooleanCombobox extends ComboBox<Boolean> {

    public BooleanCombobox() {
        Boolean[] items = {true,false};
        setItems(items);
    }


}
