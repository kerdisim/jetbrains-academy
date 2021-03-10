package flashcards

import java.io.File
import java.io.FileNotFoundException
import java.util.Random

val cards: MutableMap<String, String> = mutableMapOf()
val mistakes: MutableMap<String, Int> = mutableMapOf()
val ioLog = mutableListOf<String>()

fun printlnAndAddToLog(message: String) {
    println(message)
    ioLog.add(message)
}

fun readLineAndAddToLog(): String {
    val s = readLine()!!
    ioLog.add(s)
    return s
}

fun import(fileName: String) {
    try {
        val file = File(fileName)
        var linesCount = 0

        file.forEachLine {
            val (importedDefinition, importedTerm, importedMistakes) = it.split(';')

            // if you import a card with a term that already exists, rewrite the definition
            if (cards.containsValue(importedTerm)) {
                for ((definition, term) in cards) {
                    if (term == importedTerm) {
                        cards.remove(definition)
                        break
                    }
                }
            }

            cards[importedDefinition] = importedTerm
            mistakes[importedTerm] = importedMistakes.toInt()
            linesCount++
        }

        printlnAndAddToLog("$linesCount cards have been loaded.")
    } catch (e: FileNotFoundException) {
        printlnAndAddToLog("File not found.")
    }
}

fun export(fileName: String) {
    val file = File(fileName)
    file.writeText("")

    for ((definition, term) in cards) {
        file.appendText("$definition;$term;${mistakes[term]}\n")
    }

    printlnAndAddToLog("${cards.size} cards have been saved.")
}

fun main(args: Array<String>) {
    if ("-import" in args) {
        val fileName = args[args.indexOf("-import") + 1]
        import(fileName)
    }

    while (true) {
        printlnAndAddToLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")

        when (readLineAndAddToLog()) {
            "add" -> {
                // create a new flashcard with a unique term and definition
                printlnAndAddToLog("The card:")

                val term = readLineAndAddToLog()

                // the user tries to add a duplicate term
                if (cards.containsValue(term)) {
                    printlnAndAddToLog("The card \"$term\" already exists.")
                    continue
                }

                printlnAndAddToLog("The definition of the card:")

                val definition = readLineAndAddToLog()

                // the user tries to add a duplicate definition
                if (cards.containsKey(definition)) {
                    printlnAndAddToLog("The definition \"$definition\" already exists.")
                    continue
                }

                cards[definition] = term
                mistakes[term] = 0

                printlnAndAddToLog("The pair (\"$term\":\"$definition\") has been added.")
            }
            "remove" -> {
                // remove the card by it's term
                printlnAndAddToLog("Which card?")
                val userTerm = readLineAndAddToLog()

                if (cards.containsValue(userTerm)) {
                    for ((definition, term) in cards) {
                        if (term == userTerm) {
                            cards.remove(definition)
                            break
                        }
                    }

                    mistakes.remove(userTerm)

                    printlnAndAddToLog("The card has been removed.")
                } else {
                    printlnAndAddToLog("Can't remove \"$userTerm\": there is no such card.")
                }
            }
            "import" -> {
                printlnAndAddToLog("File name:")
                val fileName = readLineAndAddToLog()
                import(fileName)
            }
            "export" -> {
                printlnAndAddToLog("File name:")
                val fileName = readLineAndAddToLog()
                export(fileName)
            }
            "ask" -> {
                if (cards.isEmpty()) {
                    printlnAndAddToLog("There are no cards to be asked about.")
                    continue
                }

                printlnAndAddToLog("How many times to ask?")

                repeat(readLineAndAddToLog().toInt()) {
                    val (definition, term) = cards.entries.elementAt(Random().nextInt(cards.size))

                    printlnAndAddToLog("Print the definition of \"$term\"")
                    val answer = readLineAndAddToLog()

                    if (answer == definition) {
                        printlnAndAddToLog("Correct!")
                    } else {
                        val originalMistakeCount = mistakes[term]

                        if (originalMistakeCount != null) {
                            mistakes[term] = originalMistakeCount + 1
                        }

                        if (cards.containsKey(answer)) {
                            // the answer is correct for another definition
                            printlnAndAddToLog(
                                "Wrong. The right answer is \"$definition\", but your definition is correct for \"${cards[answer]}\"."
                            )
                        } else {
                            printlnAndAddToLog("Wrong. The right answer is \"$definition\".")
                        }
                    }
                }
            }
            "exit" -> {
                printlnAndAddToLog("Bye bye!")
                if ("-export" in args) {
                    val fileName = args[args.indexOf("-export") + 1]
                    export(fileName)
                }
                break
            }
            "log" -> {
                printlnAndAddToLog("File name:")
                val fileName = readLineAndAddToLog()
                val file = File(fileName)
                file.writeText(ioLog.joinToString("\n", postfix = "\n"))
                printlnAndAddToLog("The log has been saved.")
            }
            "hardest card" -> {
                var mostMistakes = 0
                val termsWithMostMistakes = mutableListOf<String>()

                for ((term, mistakeCount) in mistakes) {
                    if (mistakeCount > 0) {
                        if (mistakeCount > mostMistakes) {
                            mostMistakes = mistakeCount
                            termsWithMostMistakes.clear()
                            termsWithMostMistakes.add(term)
                        } else if (mistakeCount == mostMistakes) {
                            termsWithMostMistakes.add(term)
                        }
                    }
                }

                when (termsWithMostMistakes.size) {
                    0 -> printlnAndAddToLog("There are no cards with errors.")
                    1 -> {
                        printlnAndAddToLog(
                            "The hardest card is \"${termsWithMostMistakes[0]}\". " +
                                    "You have $mostMistakes errors answering it."
                        )
                    }
                    else -> {
                        printlnAndAddToLog(
                            "The hardest cards are ${
                                termsWithMostMistakes.joinToString { term: String -> '"' + term + '"' }
                            }. You have $mostMistakes errors answering them."
                        )
                    }
                }
            }
            "reset stats" -> {
                for (term in mistakes.keys) {
                    mistakes[term] = 0
                }
                printlnAndAddToLog("Card statistics have been reset.")
            }
        }
    }
}
