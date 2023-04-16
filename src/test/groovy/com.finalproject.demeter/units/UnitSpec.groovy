package com.finalproject.demeter.units

import spock.lang.Specification

class UnitSpec extends Specification{
    private final Unit lenEnum = LengthUnit.DEFAULT
    private final Unit weightEnum = WeightUnit.DEFAULT
    private final Unit volumeEnum = VolumetricUnit.DEFAULT
    private final Unit tempEnum = TemperatureUnit.DEFAULT
    private final Unit singEnum = SingularUnit.DEFAULT

    def "given a string, the correct length enum should be returned" (String unit, Unit unitEnum) {
        expect:
        lenEnum.getUnitEnum(unit) == unitEnum

        where:
        unit     |   unitEnum
        "m"      |   LengthUnit.M
        "meter"  |   LengthUnit.M
        "meters" |   LengthUnit.M
        "in"     |   LengthUnit.IN
        "inch"   |   LengthUnit.IN
        "inches" |   LengthUnit.IN
        "ft"     |   LengthUnit.FT
        "feet"   |   LengthUnit.FT
        "yd"     |   LengthUnit.YRD
        "yrd"    |   LengthUnit.YRD
        "yrds"   |   LengthUnit.YRD
        "yard"   |   LengthUnit.YRD
        "yards"  |   LengthUnit.YRD
        "mi"     |   LengthUnit.MI
        "mile"   |   LengthUnit.MI
        "miles"  |   LengthUnit.MI
        "else"   |   null
    }


    def "given a string, the correct weight enum should be returned" (String unit, Unit unitEnum) {
        expect:
        weightEnum.getUnitEnum(unit) == unitEnum

        where:
        unit        |   unitEnum
        "g"         |   WeightUnit.G
        "gram"      |   WeightUnit.G
        "grams"     |   WeightUnit.G
        "kg"        |   WeightUnit.KG
        "kilogram"  |   WeightUnit.KG
        "kilograms" |   WeightUnit.KG
        "mg"        |   WeightUnit.MG
        "milligram" |   WeightUnit.MG
        "milligrams"|   WeightUnit.MG
        "oz"        |   WeightUnit.OZ
        "ounce"     |   WeightUnit.OZ
        "ounces"    |   WeightUnit.OZ
        "lb"        |   WeightUnit.LB
        "lbs"       |   WeightUnit.LB
        "pound"     |   WeightUnit.LB
        "pounds"    |   WeightUnit.LB
        "st"        |   WeightUnit.ST
        "sts"       |   WeightUnit.ST
        "stone"     |   WeightUnit.ST
        "stones"    |   WeightUnit.ST
        "t"         |   WeightUnit.T
        "ts"        |   WeightUnit.T
        "ton"       |   WeightUnit.T
        "tons"      |   WeightUnit.T
        "else"      |   null
    }

    def "given a string, the correct volumetric enum should be returned" (String unit, Unit unitEnum) {
        expect:
        volumeEnum.getUnitEnum(unit) == unitEnum

        where:
        unit           |   unitEnum
        "liter"        |   VolumetricUnit.L
        "liters"       |   VolumetricUnit.L
        "l"            |   VolumetricUnit.L
        "milliliter"   |   VolumetricUnit.ML
        "milliliters"  |   VolumetricUnit.ML
        "ml"           |   VolumetricUnit.ML
        "fl oz"        |   VolumetricUnit.FL_OZ
        "floz"         |   VolumetricUnit.FL_OZ
        "fluid ounce"  |   VolumetricUnit.FL_OZ
        "fluid ounces" |   VolumetricUnit.FL_OZ
        "pint"         |   VolumetricUnit.PINT
        "pints"        |   VolumetricUnit.PINT
        "pt"           |   VolumetricUnit.PINT
        "quart"        |   VolumetricUnit.QUART
        "quarts"       |   VolumetricUnit.QUART
        "qt"           |   VolumetricUnit.QUART
        "gallon"       |   VolumetricUnit.GAL
        "gallons"      |   VolumetricUnit.GAL
        "gal"          |   VolumetricUnit.GAL
        "cup"          |   VolumetricUnit.CUP
        "cups"         |   VolumetricUnit.CUP
        "c"            |   VolumetricUnit.CUP
        "tablespoon"   |   VolumetricUnit.TBSP
        "tablespoons"  |   VolumetricUnit.TBSP
        "tbsp"         |   VolumetricUnit.TBSP
        "tbsps"        |   VolumetricUnit.TBSP
        "tbs"          |   VolumetricUnit.TBSP
        "teaspoon"     |   VolumetricUnit.TSP
        "teaspoons"    |   VolumetricUnit.TSP
        "tsp"          |   VolumetricUnit.TSP
        "tsps"         |   VolumetricUnit.TSP
        "pinch"        |   VolumetricUnit.PINCH
        "pinches"      |   VolumetricUnit.PINCH
        "dash"         |   VolumetricUnit.DASH
        "dashes"       |   VolumetricUnit.DASH
        "dusting"      |   VolumetricUnit.DUSTING
        "dustings"     |   VolumetricUnit.DUSTING
        "else"         |   null
    }

    def "given a string, the correct temp enum should be returned" (String unit, Unit unitEnum) {
        expect:
        tempEnum.getUnitEnum(unit) == unitEnum

        where:
        unit         |   unitEnum
        "c"          |   TemperatureUnit.C
        "°c"         |   TemperatureUnit.C
        "celsius"    |   TemperatureUnit.C
        "fahrenheit" |   TemperatureUnit.F
        "f"          |   TemperatureUnit.F
        "°f"         |   TemperatureUnit.F
        "kelvin"     |   TemperatureUnit.K
        "kelvins"    |   TemperatureUnit.K
        "k"          |   TemperatureUnit.K
        "else"       |   null
    }

    def "given a string, the correct singular enum should be returned" (String unit, Unit unitEnum) {
        expect:
        singEnum.getUnitEnum(unit) == unitEnum

        where:
        unit         |   unitEnum
        "piece"      |   SingularUnit.PIECE
        "pieces"     |   SingularUnit.PIECE
        "slice"      |   SingularUnit.SLICE
        "slices"     |   SingularUnit.SLICE
        "else"       |   null
    }
}
