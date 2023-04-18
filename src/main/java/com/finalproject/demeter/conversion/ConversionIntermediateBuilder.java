package com.finalproject.demeter.conversion;

import com.finalproject.demeter.units.Unit;

public class ConversionIntermediateBuilder {
    private ConversionIntermediate ci = new ConversionIntermediate();

    public ConversionIntermediateBuilder quantity(Float quantity){
        ci.setQuantity(quantity);
        return this;
    }

    public ConversionIntermediateBuilder unit(Unit unit) {
        ci.setUnit(unit);
        return this;
    }

    public ConversionIntermediate build() {
        return ci;
    }
}
