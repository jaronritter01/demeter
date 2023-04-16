package com.finalproject.demeter.units;

public enum LengthUnit implements Unit {
    M, IN, FT, YRD, MI, DEFAULT;

    @Override
    public UNIT_TYPE getUnitType() {
        return UNIT_TYPE.LENGTH;
    }

    @Override
    public LengthUnit getUnitEnum(String value) {
        return switch (value.toLowerCase()) {
            case "m", "meter", "meters" -> M; // Default Value
            case "in", "inch", "inches" -> IN;
            case "ft", "feet" -> FT;
            case "yd", "yrd", "yrds", "yard", "yards" -> YRD;
            case "mi", "mile", "miles" -> MI;
            default -> null;
        };
    }
}
