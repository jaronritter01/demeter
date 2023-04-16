package com.finalproject.demeter.conversion;


import com.finalproject.demeter.dto.Measurements;
import com.finalproject.demeter.units.*;
import com.finalproject.demeter.util.MeasurementsBuilder;

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
        Unit unit = getUnit(unitToConvert);
        if (unit == null) {
            return new MeasurementsBuilder().units("default").quantity(quantity).build();
        }

        if (quantity != null) {
            return ConversionValues.getConversionToStandard(unit).apply(quantity);
        }

        return new MeasurementsBuilder().units("default").quantity(0F).build();
    }

    private static Unit getUnit(String unitToConvert) {
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
}
