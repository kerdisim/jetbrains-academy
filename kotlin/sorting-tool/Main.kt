package sorting

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val scanner = if (args.contains("-inputFile")) {
        val fileName = args[args.indexOf("-inputFile") + 1]
        val file = File(fileName)
        Scanner(file)
    } else {
        Scanner(System.`in`)
    }

    val outputFile = if (args.contains("-outputFile")) {
        val fileName = args[args.indexOf("-outputFile") + 1]
        File(fileName)
    } else {
        null
    }

    fun output(string: String) {
        if (outputFile != null) {
            outputFile.appendText(string + '\n')
        } else {
            println(string)
        }
    }

    fun sortStringsByCount(strings: MutableList<String>) {
        strings.groupingBy { it }.eachCount().toList()
            .sortedWith { a: Pair<String, Int>, b: Pair<String, Int> ->
                if (a.second == b.second) {
                    if (a.first > b.first) {
                        1
                    } else {
                        -1
                    }
                } else {
                    a.second - b.second
                }
            }.forEach { (string, count) ->
                output("$string: $count time(s), ${count * 100 / strings.size}%")
            }
    }

    val sortingType = if (args.contains("-sortingType")) {
        val sortingTypeIdx = args.indexOf("-sortingType") + 1
        if (sortingTypeIdx > args.lastIndex || !listOf("natural", "byCount").contains(args[sortingTypeIdx])) {
            println("No sorting type defined!")
            return
        }
        args[sortingTypeIdx]
    } else {
        "natural"
    }

    val dataType = if (args.contains("-dataType")) {
        val dataTypeIdx = args.indexOf("-dataType") + 1
        if (dataTypeIdx > args.lastIndex || !listOf("long", "word", "line").contains(args[dataTypeIdx])) {
            println("No data type defined!")
            return
        }
        args[dataTypeIdx]
    } else {
        "word"
    }

    val invalidParameters = args.filter { it.first() == '-' && !listOf("-dataType", "-sortingType").contains(it) }

    for (param in invalidParameters) {
        println("\"$param\" is not a valid parameter. It will be skipped.")
    }

    when (dataType) {
        "long" -> {
            val numbers: MutableList<Int> = mutableListOf()
            while (scanner.hasNext()) {
                val next = scanner.next()
                try {
                    numbers.add(next.toInt())
                } catch (e: NumberFormatException) {
                    println("\"$next\" is not a long. It will be skipped.")
                }
            }
            output("Total numbers ${numbers.size}.")

            when (sortingType) {
                "byCount" -> {
                    numbers.groupingBy { it }.eachCount().toList()
                        .sortedWith { a: Pair<Int, Int>, b: Pair<Int, Int> ->
                            if (a.second == b.second) {
                                a.first - b.first
                            } else {
                                a.second - b.second
                            }
                        }
                        .forEach { (number, count) ->
                            output("$number: $count time(s), ${count * 100 / numbers.size}%")
                        }
                }
                "natural" -> {
                    numbers.sort()
                    output("Sorted data: ${numbers.joinToString(" ")}")
                }
            }
        }
        "word" -> {
            val words: MutableList<String> = mutableListOf()
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                words.addAll(line.split("\\s+".toRegex()))
            }
            output("Total words ${words.size}")

            when (sortingType) {
                "byCount" -> {
                    sortStringsByCount(words)
                }
                "natural" -> {
                    words.sort()
                    output("Sorted data: ${words.joinToString(" ")}")
                }
            }
        }
        "line" -> {
            val lines: MutableList<String> = mutableListOf()
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine())
            }
            output("Total lines: ${lines.size}")

            when (sortingType) {
                "byCount" -> {
                    sortStringsByCount(lines)
                }
                "natural" -> {
                    lines.sort()
                    output("Sorted data:\n${lines.joinToString("\n")}")
                }
            }
        }
    }
}
