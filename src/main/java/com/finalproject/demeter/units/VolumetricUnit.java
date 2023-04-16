package com.finalproject.demeter.units;

public enum VolumetricUnit implements Unit {
    L, ML, FL_OZ, PINT, QUART, GAL, CUP, TBSP, TSP, PINCH, DASH, DUSTING, DEFAULT;

    @Override
    public UNIT_TYPE getUnitType() {
        return UNIT_TYPE.VOLUMETRIC;
    }

    @Override
    public VolumetricUnit getUnitEnum(String value) {
        return switch (value.toLowerCase()) {
            case "liter", "liters", "l" -> L; // current standard unit
            case "milliliter", "milliliters", "ml" -> ML;
            case "fl oz", "floz", "fluid ounce", "fluid ounces" -> FL_OZ;
            case "pint", "pints", "pt" -> PINT;
            case "quart", "quarts", "qt" -> QUART;
            case "gallon", "gallons", "gal" -> GAL;
            case "cup", "cups", "c" -> CUP;
            case "tablespoon", "tablespoons", "tbsp", "tbsps", "tbs" -> TBSP;
            case "teaspoon", "teaspoons", "tsp", "tsps" -> TSP;
            case "pinch", "pinches" -> PINCH;
            case "dash", "dashes" -> DASH;
            case "dusting", "dustings" -> DUSTING;
            default -> null; // can be used to check if the measurement is volumetric
        };
    }
}
