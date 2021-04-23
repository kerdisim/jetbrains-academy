package search

import java.io.File

fun main(args: Array<String>) {
    val idx = args.indexOf("--data")
    if (idx == -1) {
        println("Command line argument --data was not provided.")
    }
    if (args.lastIndex == idx) {
        println("File name was not provided.")
    }

    val people = mutableListOf<String>()
    File(args[idx + 1]).forEachLine { people.add(it) }

    val searchIndex = mutableMapOf<String, MutableList<Int>>()

    for (i in people.indices) {
        val words = people[i].toLowerCase().split(" ")
        for (word in words) {
            if (searchIndex.contains(word)) {
                searchIndex[word]!!.add(i)
            } else {
                searchIndex[word] = mutableListOf(i)
            }
        }
    }

    while (true) {
        println(
            """
                === Menu ===
                1. Find a person
                2. Print all people
                0. Exit
            """.trimIndent()
        )
        when (readLine()!!) {
            "1" -> {
                println("Select a matching strategy: ALL, ANY, NONE")
                val strategy = readLine()!!

                println("Enter a name or email to search all suitable people.")
                val wordsIndices = readLine()!!.toLowerCase().split(" ").mapNotNull { word -> searchIndex[word] }

                if (wordsIndices.isEmpty()) {
                    println("No matching people found.")
                    continue
                }

                val linesIndices = when (strategy) {
                    "ALL" -> {
                        // lines containing all the words from the query
                        wordsIndices
                            .reduce { acc, indexList -> (acc intersect indexList).toMutableList() }
                    }
                    "ANY" -> {
                        // lines containing at least one word from the query
                        wordsIndices
                            .reduce { acc, indexList -> (acc union indexList).toMutableList() }
                    }
                    "NONE" -> {
                        // lines that do not contain word from the query at all
                        people.indices subtract wordsIndices
                            .reduce { acc, indexList -> (acc union indexList).toMutableList() }
                    }
                    else -> {
                        println("Unknown strategy.")
                        listOf()
                    }
                }

                if (linesIndices.isEmpty()) {
                    println("No matching people found.")
                } else {
                    println("${linesIndices.size} persons found:")
                    for (i in linesIndices) {
                        println(people[i])
                    }
                }
            }
            "2" -> {
                println("=== List of people ===")
                println(people.joinToString("\n"))
            }
            "0" -> {
                println("Bye!")
                return
            }
            else -> {
                println("Incorrect option! Try again.")
            }
        }
    }
}
