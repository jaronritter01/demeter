package com.finalproject.demeter.units;

public enum TemperatureUnit implements Unit{
    F, C, K, DEFAULT;

    @Override
    public UNIT_TYPE getUnitType() {
        return UNIT_TYPE.TEMPERATURE;
    }

    @Override
    public TemperatureUnit getUnitEnum(String value) {
        return switch (value.toLowerCase()) {
            case "celsius", "c", "°c" -> C; // current standard unit
            case "fahrenheit", "f", "°f" -> F;
            case "kelvin", "kelvins", "k" -> K;
            default -> null;
        };
    }
}
