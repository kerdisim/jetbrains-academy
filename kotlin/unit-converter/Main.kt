package converter

enum class UnitType {
    LENGTH, WEIGHT, TEMPERATURE
}

enum class Unit(val type: UnitType, val names: List<String>, val baseFactor: Double) {
    METER(UnitType.LENGTH, listOf("m", "meter", "meters"), 1.0),
    KILOMETER(UnitType.LENGTH, listOf("km", "kilometer", "kilometers"), 1000.0),
    CENTIMETER(UnitType.LENGTH, listOf("cm", "centimeter", "centimeters"), 0.01),
    MILLIMETER(UnitType.LENGTH, listOf("mm", "millimeter", "millimeters"), 0.001),
    MILE(UnitType.LENGTH, listOf("mi", "mile", "miles"), 1609.35),
    YARD(UnitType.LENGTH, listOf("yd", "yard", "yards"), 0.9144),
    FOOT(UnitType.LENGTH, listOf("ft", "foot", "feet"), 0.3048),
    INCH(UnitType.LENGTH, listOf("in", "inch", "inches"), 0.0254),
    GRAM(UnitType.WEIGHT, listOf("g", "gram", "grams"), 1.0),
    KILOGRAM(UnitType.WEIGHT, listOf("kg", "kilogram", "kilograms"), 1000.0),
    MILLIGRAM(UnitType.WEIGHT, listOf("mg", "milligram", "milligrams"), 0.001),
    POUNDS(UnitType.WEIGHT, listOf("lb", "pound", "pounds"), 453.592),
    OUNCES(UnitType.WEIGHT, listOf("oz", "ounce", "ounces"), 28.3495),
    CELSIUS(
        UnitType.TEMPERATURE,
        listOf("c", "degree Celsius", "degrees Celsius", "celsius", "dc"),
        0.0
    ),
    FAHRENHEIT(
        UnitType.TEMPERATURE,
        listOf("f", "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df"),
        0.0
    ),
    KELVIN(UnitType.TEMPERATURE, listOf("k", "kelvin", "kelvins"), 0.0);

    fun quantityWithUnit(quantity: Double): String {
        return if (quantity == 1.0) {
            "$quantity ${names[1]}"
        } else {
            "$quantity ${names[2]}"
        }
    }

    companion object {
        fun findByName(name: String): Unit? {
            for (enum in values()) {
                if (name in enum.names) {
                    return enum
                }
            }
            return null
        }

        fun plural(unit: Unit?): String {
            return if (unit == null) {
                "???"
            } else {
                unit.names[2]
            }
        }

        fun convert(quantity: Double, from: Unit, to: Unit): Double {
            if (from == to) {
                return quantity
            }

            if (from.type != to.type) {
                throw Exception("Units must be of the same type.")
            }

            return when (from.type) {
                UnitType.TEMPERATURE -> {
                    convertTemperature(quantity, from, to)
                }
                else -> {
                    quantity * from.baseFactor / to.baseFactor
                }
            }
        }

        private fun convertTemperature(quantity: Double, from: Unit, to: Unit): Double {
            return when (from) {
                CELSIUS -> {
                    when (to) {
                        FAHRENHEIT -> {
                            quantity * 9.0 / 5.0 + 32.0
                        }
                        KELVIN -> {
                            quantity + 273.15
                        }
                        else -> {
                            throw Exception()
                        }
                    }
                }
                FAHRENHEIT -> {
                    when (to) {
                        CELSIUS -> {
                            (quantity - 32.0) * 5.0 / 9.0
                        }
                        KELVIN -> {
                            (quantity + 459.67) * 5.0 / 9.0
                        }
                        else -> {
                            throw Exception()
                        }
                    }
                }
                KELVIN -> {
                    when (to) {
                        CELSIUS -> {
                            quantity - 273.15
                        }
                        FAHRENHEIT -> {
                            quantity * 9.0 / 5.0 - 459.67
                        }
                        else -> {
                            throw Exception()
                        }
                    }
                }
                else -> {
                    throw Exception()
                }
            }
        }
    }
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()!!

        if (input == "exit") {
            return
        }

        val args = input.split(" ")

        if (args.isEmpty()) {
            println("Parse error")
            continue
        }

        var quantity: Double

        try {
            quantity = args[0].toDouble()
        } catch (e: NumberFormatException) {
            println("Parse error")
            continue
        }

        val unitFrom: Unit? =
            if (args[1].toLowerCase() == "degree" || args[1].toLowerCase() == "degrees") {
                Unit.findByName(args[2].toLowerCase())
            } else {
                Unit.findByName(args[1].toLowerCase())
            }

        val unitToName =
            if (args[1].toLowerCase() == "degree" || args[1].toLowerCase() == "degrees") {
                if (args.lastIndex >= 5 && args[4].toLowerCase() == "degree" || args[4].toLowerCase() == "degrees") {
                    args[5]
                } else {
                    args[4]
                }
            } else {
                if (args.lastIndex >= 4 && args[3].toLowerCase() == "degree" || args[3].toLowerCase() == "degrees") {
                    args[4]
                } else {
                    args[3]
                }

            }

        val unitTo: Unit? = Unit.findByName(unitToName.toLowerCase())

        if (unitFrom == null || unitTo == null) {
            println("Conversion from ${Unit.plural(unitFrom)} to ${Unit.plural(unitTo)} is impossible")
        } else {
            if (quantity < 0) {
                when (unitFrom.type) {
                    UnitType.WEIGHT -> {
                        println("Weight shouldn't be negative")
                        continue
                    }
                    UnitType.LENGTH -> {
                        println("Length shouldn't be negative")
                        continue
                    }
                    UnitType.TEMPERATURE -> {
                        // temperature can be negative
                    }
                }
            }

            try {
                println(
                    "${unitFrom.quantityWithUnit(quantity)} is ${
                        unitTo.quantityWithUnit(
                            Unit.convert(
                                quantity,
                                unitFrom,
                                unitTo
                            )
                        )
                    }"
                )
            } catch (e: Exception) {
                println("Conversion from ${Unit.plural(unitFrom)} to ${Unit.plural(unitTo)} is impossible")
            }
        }
    }
}
