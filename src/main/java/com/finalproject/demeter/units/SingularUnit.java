package com.finalproject.demeter.units;

public enum SingularUnit implements Unit {
    SLICE, PIECE, DEFAULT;

    @Override
    public UNIT_TYPE getUnitType(){
        return UNIT_TYPE.SINGULAR;
    }

    @Override
    public SingularUnit getUnitEnum(String value) {
        return switch (value.toLowerCase()) {
            case "piece", "pieces" -> PIECE; // Default Value
            case "slice", "slices" -> SLICE;
            default -> null;
        };
    }
}
