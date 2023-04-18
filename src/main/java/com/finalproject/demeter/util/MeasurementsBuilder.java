package com.finalproject.demeter.util;

import com.finalproject.demeter.dto.Measurements;

public class MeasurementsBuilder {
    private Measurements measurements = new Measurements();

    public MeasurementsBuilder units(String units) {
        measurements.setUnit(units);
        return this;
    }

    public MeasurementsBuilder quantity(Float quantity){
        measurements.setQuantity(quantity);
        return this;
    }

    public Measurements build() {
        return measurements;
    }
}
