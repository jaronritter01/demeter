package com.finalproject.demeter.conversion;


import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.RecipeItem;
import com.finalproject.demeter.dto.Measurements;
import com.finalproject.demeter.dto.PersonalRecipeItem;
import com.finalproject.demeter.units.*;
import com.finalproject.demeter.util.MeasurementsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConversionUtils {
    private static final VolumetricUnit volumetricUnit = VolumetricUnit.DEFAULT;
    private static final LengthUnit lengthUnit = LengthUnit.DEFAULT;
    private static final SingularUnit singularUnit = SingularUnit.DEFAULT;
    private static final TemperatureUnit temperatureUnit = TemperatureUnit.DEFAULT;
    private static final WeightUnit weightUnit = WeightUnit.DEFAULT;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionUtils.class);

    public static void convertInventory(List<InventoryItem> inventory, boolean isMetric) {
        for (InventoryItem item : inventory) {
            try {
                Measurements newMeasure = convertFromStandardUnit(item.getUnit(), item.getQuantity(), isMetric);
                item.setUnit(newMeasure.getUnit());
                item.setQuantity(newMeasure.getQuantity());
            } catch (Exception e) {
                LOGGER.error("Inventory Item: {} with quantity {} and unit {} cannot be converted. Error: {}",
                        item.getId(), item.getQuantity(), item.getUnit(), e.toString());
            }
        }
    }

    public static void convertRecipeItems(List<RecipeItem> items, boolean isMetric) {
        for (RecipeItem item : items) {
            try {
                Measurements newMeasure = convertFromStandardUnit(item.getMeasurementUnit(), item.getQuantity(), isMetric);
                item.setMeasurementUnit(newMeasure.getUnit());
                item.setQuantity(newMeasure.getQuantity());
            } catch (Exception e) {
                LOGGER.error("Inventory Item: {} with quantity {} and unit {} cannot be converted. Error: {}",
                        item.getId(), item.getQuantity(), item.getMeasurementUnit(), e.toString());
            }
        }
    }

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

    public static Measurements convertFromStandardUnit(String unitToConvert, Float quantity, boolean isMetric)
            throws Exception {
        Unit initialUnit = null;
        if (!isMetric) {
            initialUnit = getUnitOutImperial(unitToConvert); // What determines if it's metric or us
        } else {
            initialUnit = getUnitOutMetric(unitToConvert);
        }

        if (initialUnit == null) {
            throw new Exception("Unit not Found");
        }

        if (quantity == null) {
            throw new Exception("No Quantity Supplied");
        }

        Unit finalUnit = null;
        while (finalUnit != initialUnit) {
            finalUnit = initialUnit;
            ConversionIntermediate ci = ConversionValues.getConversionFromStandard(initialUnit).apply(quantity);
            initialUnit = ci.getUnit();
            quantity = ci.getQuantity();
        }

        String returnUnit = getUnitString(finalUnit, quantity);
        return new MeasurementsBuilder().units(returnUnit).quantity(quantity).build();
    }

    private static String getUnitString(Unit unit, Float quantity) {
        if (unit.equals(LengthUnit.IN)) {
            return "in";
        } else if (unit.equals(LengthUnit.FT)) {
            return "ft";
        } else if (unit.equals(LengthUnit.YRD)) {
            return "yd";
        } else if (unit.equals(LengthUnit.M)) {
            return "m";
        }

        else if (unit.equals(SingularUnit.SLICE)) {
            if (quantity > 1) {
                return "slices";
            }
            return "slice";
        } else if (unit.equals(SingularUnit.PIECE)) {
            if (quantity > 1) {
                return "pieces";
            }
            return "piece";
        }

        else if (unit.equals(TemperatureUnit.F)) {
            return "f";
        } else if (unit.equals(TemperatureUnit.C)) {
            return "c";
        }

        else if (unit.equals(VolumetricUnit.TSP)) {
            return "tsp";
        } else if (unit.equals(VolumetricUnit.TBSP)) {
            return "Tbsp";
        } else if (unit.equals(VolumetricUnit.CUP)) {
            if (quantity > 1) {
                return "cups";
            }
            return "cup";
        } else if (unit.equals(VolumetricUnit.GAL)) {
            return "gal";
        } else if (unit.equals(VolumetricUnit.ML)) {
            return "ml";
        } else if (unit.equals(VolumetricUnit.L)) {
            return "L";
        }


        else if (unit.equals(WeightUnit.OZ)) {
            return "oz";
        } else if (unit.equals(WeightUnit.LB)) {
            return "lb";
        } else if (unit.equals(WeightUnit.MG)) {
            return "mg";
        } else if (unit.equals(WeightUnit.G)) {
            return "g";
        } else if (unit.equals(WeightUnit.KG)) {
            return "kg";
        }

        return "default";
    }

    private static Unit getUnitOutMetric(String unitToConvert) {
        return switch (unitToConvert.toLowerCase()) {
            case "m", "meter", "meters" -> LengthUnit.M;
            case "peice", "pieces" -> SingularUnit.PIECE;
            case "celsius", "c", "°c" -> TemperatureUnit.C;
            case "liter", "liters", "l" -> VolumetricUnit.L;
            case "gram", "grams", "g" -> WeightUnit.G;
            default -> null;
        };
    }

    private static Unit getUnitOutImperial(String unitToConvert) {
        return switch (unitToConvert.toLowerCase()) {
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
