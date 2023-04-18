package com.finalproject.demeter.units;

public enum WeightUnit implements Unit {
    G, KG, MG, OZ, LB, ST, T, DEFAULT;

    @Override
    public UNIT_TYPE getUnitType() {
        return UNIT_TYPE.MASS;
    }

    @Override
    public WeightUnit getUnitEnum(String value) {
        return switch (value.toLowerCase()) {
            case "gram", "grams", "g" -> G; // Default Value
            case "kilogram", "kilograms", "kg" -> KG;
            case "milligram", "milligrams", "mg" -> MG;
            case "ounce", "ounces", "oz" -> OZ;
            case "pound", "pounds", "lb", "lbs" -> LB;
            case "stone", "stones", "st", "sts" -> ST;
            case "ton", "tons", "t", "ts" -> T;
            default -> null;
        };
    }
}
