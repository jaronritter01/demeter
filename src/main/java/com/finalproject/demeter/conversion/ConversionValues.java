package com.finalproject.demeter.conversion;

import com.finalproject.demeter.dto.Measurements;
import com.finalproject.demeter.units.Unit;
import com.finalproject.demeter.util.MeasurementsBuilder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.finalproject.demeter.units.LengthUnit.*;
import static com.finalproject.demeter.units.SingularUnit.PIECE;
import static com.finalproject.demeter.units.SingularUnit.SLICE;
import static com.finalproject.demeter.units.TemperatureUnit.*;
import static com.finalproject.demeter.units.VolumetricUnit.*;
import static com.finalproject.demeter.units.WeightUnit.*;

public class ConversionValues {
    private final static Map<Unit, Function<Float, ConversionIntermediate>> conversionFromStandardMap = new HashMap<>() {{
        // Length Conversions
        put(IN, ConversionValues::meterToInches);
        put(FT, ConversionValues::inchesToFoot);
        put(YRD, ConversionValues::footToYard);
        // Singular conversions
        put(SLICE, ConversionValues::pieceToSlice);
        // Temp Conversions
        put(F, ConversionValues::celsiusToFahrenheit);
        // Volume Conversions
        put(TSP, ConversionValues::literToTeaspoon);
        put(TBSP, ConversionValues::teaspoonToTablespoon);
        put(CUP, ConversionValues::tablespoonToCup);
        put(GAL, ConversionValues::cupToGallon);
        // Weight conversions
        put(OZ, ConversionValues::gramToOunce);
        put(LB, ConversionValues::ounceToPound);
    }};

    private final static Map<Unit, Function<Float, Measurements>> conversionToStandardMap = new HashMap<>() {{
        put(M, ConversionValues::meterToMeter);
        put(IN, ConversionValues::inchesToMeter);
        put(FT, ConversionValues::footToMeter);
        put(YRD, ConversionValues::yardToMeter);
        put(MI, ConversionValues::mileToMeter);
        put(SLICE, ConversionValues::sliceToPiece);
        put(PIECE, ConversionValues::pieceToPiece);
        put(F, ConversionValues::fahrenheitToCelsius);
        put(C, ConversionValues::celsiusToCelsius);
        put(K, ConversionValues::kelvinToCelsius);
        put(L, ConversionValues::literToLiter);
        put(ML, ConversionValues::milliliterToLiter);
        put(FL_OZ, ConversionValues::fluidOunceToLiter);
        put(PINT, ConversionValues::pintToLiter);
        put(QUART, ConversionValues::quartToLiter);
        put(GAL, ConversionValues::gallonToLiter);
        put(CUP, ConversionValues::cupToLiter);
        put(TBSP, ConversionValues::tablespoonToLiter);
        put(TSP, ConversionValues::teaspoonToLiter);
        put(PINCH, ConversionValues::pinchToLiter);
        put(DASH, ConversionValues::dashToLiter);
        put(DUSTING, ConversionValues::dustingToLiter);
        put(G, ConversionValues::gramToGram);
        put(KG, ConversionValues::kilogramToGram);
        put(MG, ConversionValues::milligramToGram);
        put(OZ, ConversionValues::ounceToGram);
        put(LB, ConversionValues::poundToGram);
        put(ST, ConversionValues::stoneToGram);
        put(T, ConversionValues::tonToGram);
    }};

    private static final DecimalFormat df = new DecimalFormat("#.000000");

    public static Function<Float, Measurements> getConversionToStandard(Unit unit) {
        try {
            return conversionToStandardMap.get(unit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Function<Float, ConversionIntermediate> getConversionFromStandard(Unit unit) {
        try {
            return conversionFromStandardMap.get(unit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //START OUTBOUND MEASUREMENT CONVERSIONS

    public static ConversionIntermediate meterToInches(Float quantity) {
        Float convertedValue = Double.valueOf(quantity * 39.37).floatValue();
        if (convertedValue > 24 ) {
            return new ConversionIntermediateBuilder().quantity(convertedValue).unit(IN).build();
        }
        // This is to convert to the next level up
        return new ConversionIntermediateBuilder().quantity(convertedValue).unit(FT).build();
    }

    public static ConversionIntermediate inchesToFoot(Float quantity) {
        Float convertedValue = Double.valueOf(quantity / 12).floatValue();
        if (convertedValue > 6 ) {
            return new ConversionIntermediateBuilder().quantity(convertedValue).unit(FT).build();
        }
        // This is to convert to the next level up
        return new ConversionIntermediateBuilder().quantity(convertedValue).unit(YRD).build();
    }

    public static ConversionIntermediate footToYard(Float quantity) {
        Float convertedValue = Double.valueOf(quantity / 3).floatValue();
        // This is a final unit, it cannot be converted further
        return new ConversionIntermediateBuilder().quantity(convertedValue).unit(YRD).build();
    }

    public static ConversionIntermediate pieceToSlice(Float quantity) {
        // This is a final unit, it cannot be converted further
        return new ConversionIntermediateBuilder().quantity(quantity).unit(SLICE).build();
    }

    public static ConversionIntermediate celsiusToFahrenheit(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity * (9.0/5) + 32).floatValue();
        // This is a final unit, it cannot be converted further
        return new ConversionIntermediateBuilder().quantity(quantity).unit(F).build();
    }

    public static ConversionIntermediate literToTeaspoon(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity * 202.9).floatValue();
        if (conversionQuantity < 6) {
            return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(TSP).build();
        }

        return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(TBSP).build();
    }

    public static ConversionIntermediate teaspoonToTablespoon(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity / 3).floatValue();
        if (conversionQuantity < 6) {
            return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(TBSP).build();
        }

        return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(CUP).build();
    }

    public static ConversionIntermediate tablespoonToCup(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity / 16).floatValue();
        if (conversionQuantity < 16) {
            return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(CUP).build();
        }

        return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(GAL).build();
    }

    public static ConversionIntermediate cupToGallon(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity / 16).floatValue();
        // This is a final unit, it cannot be converted further
        return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(GAL).build();
    }

    public static ConversionIntermediate gramToOunce(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity / 28.35).floatValue();
        if (conversionQuantity < 16) {
            return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(OZ).build();
        }

        return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(LB).build();
    }

    public static ConversionIntermediate ounceToPound(Float quantity) {
        Float conversionQuantity = Double.valueOf(quantity / 16).floatValue();
        // This is a final unit, it cannot be converted further
        return new ConversionIntermediateBuilder().quantity(conversionQuantity).unit(LB).build();
    }

    //START INBOUND MEASUREMENT CONVERSIONS

    public static Measurements meterToMeter(Float quantity) {
        return new MeasurementsBuilder().units("m").quantity(quantity).build();
    }

    public static Measurements inchesToMeter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 39.37).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements footToMeter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 3.281).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements yardToMeter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 1.094).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements mileToMeter(Float quantity) {
        Float returnQuantity = quantity * 1609;
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements sliceToPiece(Float quantity) {
        return new MeasurementsBuilder().units("piece").quantity(quantity).build();
    }

    public static Measurements pieceToPiece(Float quantity) {
        return new MeasurementsBuilder().units("piece").quantity(quantity).build();
    }

    public static Measurements fahrenheitToCelsius(Float quantity) {
        // TODO: Test this to make sure it's right
        Double ratio =  5.0/9.0;
        Float returnQuantity = Double.valueOf((quantity - 32) * ratio).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("C").quantity(returnQuantity).build();
    }

    public static Measurements celsiusToCelsius(Float quantity) {
        return new MeasurementsBuilder().units("C").quantity(quantity).build();
    }

    public static Measurements kelvinToCelsius(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity - 273.15).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("C").quantity(returnQuantity).build();
    }

    public static Measurements literToLiter(Float quantity) {
        return new MeasurementsBuilder().units("L").quantity(quantity).build();
    }

    public static Measurements milliliterToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 1000).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements fluidOunceToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 33.814).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements pintToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 2.113).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements quartToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 1.057).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements gallonToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity * 3.785).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements cupToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 4.227).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements tablespoonToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 67.628).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements teaspoonToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity /202.9).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements pinchToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 32258.0645).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements dashToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 1612.9).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements dustingToLiter(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 1612.9).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements gramToGram(Float quantity) {
        return new MeasurementsBuilder().units("g").quantity(quantity).build();
    }

    public static Measurements kilogramToGram(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity * (1000)).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements milligramToGram(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity / 1000).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements ounceToGram(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity * 28.35).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements poundToGram(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity * 453.6).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements stoneToGram(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity * 6350.29).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements tonToGram(Float quantity) {
        Float returnQuantity = Double.valueOf(quantity * 907200).floatValue();
        returnQuantity = Float.valueOf(df.format(returnQuantity));
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

}
