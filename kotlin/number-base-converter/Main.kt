package converter

import java.math.BigDecimal
import java.math.BigInteger

val decimalToDigits: Map<Int, Char> = mapOf(
    0 to '0',
    1 to '1',
    2 to '2',
    3 to '3',
    4 to '4',
    5 to '5',
    6 to '6',
    7 to '7',
    8 to '8',
    9 to '9',
    10 to 'a',
    11 to 'b',
    12 to 'c',
    13 to 'd',
    14 to 'e',
    15 to 'f',
    16 to 'g',
    17 to 'h',
    18 to 'i',
    19 to 'j',
    20 to 'k',
    21 to 'l',
    22 to 'm',
    23 to 'n',
    24 to 'o',
    25 to 'p',
    26 to 'q',
    27 to 'r',
    28 to 's',
    29 to 't',
    30 to 'u',
    31 to 'v',
    32 to 'w',
    33 to 'x',
    34 to 'y',
    35 to 'z',
)
val digitsToDecimal: Map<Char, Int> = decimalToDigits.entries.associate { (key, value) -> value to key }

fun convertIntegerPartFromDecimal(number: BigInteger, base: Int): String {
    if (number.compareTo(BigInteger.ZERO) == 0) {
        return "0"
    }

    var quotient = number
    val bigIntBase = base.toBigInteger()
    val digits: MutableList<Int> = mutableListOf()

    while (quotient.compareTo(BigInteger.ZERO) == 1) {
        digits.add(quotient.mod(bigIntBase).toInt())
        quotient /= bigIntBase
    }

    return digits.map { decimalToDigits[it] }.reversed().joinToString("")
}

fun convertFractionalPartFromDecimal(number: BigDecimal, base: Int): String {
    val bigDecimalBase = base.toBigDecimal()
    val digits: MutableList<Int> = mutableListOf()

    var fractionalPart = number

    while (fractionalPart.compareTo(BigDecimal.ZERO) != 0) {
        fractionalPart *= bigDecimalBase

        val strValue = fractionalPart.toString()

        if (strValue.contains('.')) {
            val split = strValue.split('.')

            val integerPart = split[0].toInt()
            digits.add(integerPart)

            if (digits.size == 5) {
                break
            }

            fractionalPart = BigDecimal("0." + strValue.split('.')[1])
        }
    }
    return digits.map { decimalToDigits[it] }.joinToString("").padEnd(5, '0')
}

fun convertFromDecimal(number: String, base: Int, includeFractionalPart: Boolean): String {
    val split = number.split('.')
    val integerPart = BigInteger(split[0])
    val fractionalPart = BigDecimal("0." + split[1])

    return if (includeFractionalPart) {
        convertIntegerPartFromDecimal(integerPart, base) + "." + convertFractionalPartFromDecimal(
            fractionalPart,
            base
        )
    } else {
        convertIntegerPartFromDecimal(integerPart, base)
    }
}

fun convertToDecimal(number: String, base: Int): String {
    val baseBigDecimal = base.toBigDecimal()
    var result = BigDecimal.ZERO
    val split = number.split('.')
    val integerPart = split[0]
    var power: BigDecimal = baseBigDecimal.pow(integerPart.length - 1).setScale(1000)

    for (c in number) {
        if (c == '.') {
            continue
        }
        val decimalValue: Int = digitsToDecimal[c]!!
        result += decimalValue.toBigDecimal() * power
        power /= baseBigDecimal
    }

    return result.toString()
}

fun convert(number: String, sourceBase: Int, targetBase: Int): String {
    return convertFromDecimal(convertToDecimal(number, sourceBase), targetBase, number.contains('.'))
}

fun main() {
    while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        var input = readLine()!!

        if (input == "/exit") {
            return
        }

        val (sourceBase, targetBase) = input.split(" ").map { it.toInt() }

        while (true) {
            println(
                "Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)"
            )
            input = readLine()!!

            if (input == "/back") {
                break
            }

            println("Conversion result: ${convert(input, sourceBase, targetBase)}")
        }
    }
}
