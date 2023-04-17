package com.finalproject.demeter.conversion;


import com.finalproject.demeter.dto.Measurements;
import com.finalproject.demeter.dto.PersonalRecipeItem;
import com.finalproject.demeter.units.*;
import com.finalproject.demeter.util.MeasurementsBuilder;

import java.util.List;

public class ConversionUtils {
    private static final VolumetricUnit volumetricUnit = VolumetricUnit.DEFAULT;
    private static final LengthUnit lengthUnit = LengthUnit.DEFAULT;
    private static final SingularUnit singularUnit = SingularUnit.DEFAULT;
    private static final TemperatureUnit temperatureUnit = TemperatureUnit.DEFAULT;
    private static final WeightUnit weightUnit = WeightUnit.DEFAULT;

    /**
     * Used to convert non-standard units to standard units.
     * @param unitToConvert the unit of the measurement that needs to be converted.
     * @param quantity the quantity that needs converted.
     * @return a Measurements DTO with the converted measurements.
     * */
    public static Measurements convertToStandardUnit(String unitToConvert, Float quantity) {
        Unit unit = getUnitIn(unitToConvert);
        if (unit == null) {
            return new MeasurementsBuilder().units("default").quantity(quantity).build();
        }

        if (quantity != null) {
            return ConversionValues.getConversionToStandard(unit).apply(quantity);
        }

        return new MeasurementsBuilder().units("default").quantity(0F).build();
    }

    public static Measurements convertFromStandardUnit(String unitToConvert, Float quantity) throws Exception {
        Unit unit = getUnitOut(unitToConvert);
        if (unit == null) {
            throw new Exception("Unit not Found");
        }

        if (quantity == null) {
            throw new Exception("No Quantity Supplied");
        }

        //TODO: Add Fall down conversion logic
        ConversionValues.getConversionFromStandard(unit).apply(quantity);


        return new MeasurementsBuilder().units("default").quantity(0F).build();
    }

    private static Unit getUnitOut(String unitToConvert) {
        return switch (unitToConvert) {
            case "m", "meter", "meters" -> LengthUnit.IN;
            case "peice", "pieces" -> SingularUnit.SLICE;
            case "celsius", "c", "°c" -> TemperatureUnit.F;
            case "liter", "liters", "l" -> VolumetricUnit.TSP;
            case "gram", "grams", "g" -> WeightUnit.OZ;
            default -> null;
        };
    }

    private static Unit getUnitIn(String unitToConvert) {
        Unit volUnit = volumetricUnit.getUnitEnum(unitToConvert);
        Unit lenUnit = lengthUnit.getUnitEnum(unitToConvert);
        Unit singUnit = singularUnit.getUnitEnum(unitToConvert);
        Unit tempUnit = temperatureUnit.getUnitEnum(unitToConvert);
        Unit wgtUnit = weightUnit.getUnitEnum(unitToConvert);

        if (volUnit != null) {
            return volUnit;
        }

        if (lenUnit != null) {
            return lenUnit;
        }

        if (singUnit != null) {
            return singUnit;
        }

        if (tempUnit != null) {
            return tempUnit;
        }

        return wgtUnit;
    }

    /**
     * This takes in an ingredient list for a personal recipe and converts the units and quantities to standard units
     * , inplace.
     * @param ingredientList list of PersonalRecipeItems that need to be converted before internalization.
     * */
    public static void convertPersonalRecipeItems(List<PersonalRecipeItem> ingredientList) {
        // This is an inplace switch
        for (PersonalRecipeItem personalRecipeItem : ingredientList){
            Float quantity = personalRecipeItem.getQuantity();
            String unit = personalRecipeItem.getUnit();
            // find proper conversions
            Measurements convertMeasurements = ConversionUtils.convertToStandardUnit(unit, quantity);
            // set the new units and quantity
            personalRecipeItem.setQuantity(convertMeasurements.getQuantity());
            personalRecipeItem.setUnit(convertMeasurements.getUnit());
        }
    }
}
