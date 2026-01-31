package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.combobox.ComboBox;

public class BooleanCombobox extends ComboBox<Boolean> {

    public BooleanCombobox() {
        Boolean[] items = {true,false};
        setItems(items);
        setItemLabelGenerator(b -> {
            if (b==null){
                return "";
            } else if (b){
                return "Ja";
            } else if (!b) {
                return "Nei";
            } else {
                return "";
            }
        });
    }


}
