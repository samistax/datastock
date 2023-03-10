package com.example.samistax.components;

import com.example.samistax.astra.data.StockSymbol;
import com.vaadin.flow.component.combobox.ComboBox;

public class StockSymbolComboBox extends ComboBox<StockSymbol> {

    public StockSymbolComboBox(String label) {
        super();
        setLabel(label);
        setItems(StockSymbol.supportedSymbols());
        setPlaceholder("Search by company");
        setItemLabelGenerator(StockSymbol::name);
    }
}
